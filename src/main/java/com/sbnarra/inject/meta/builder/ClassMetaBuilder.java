package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.L;
import com.sbnarra.inject.meta.AspectMeta;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.MethodOverrideMatcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

@RequiredArgsConstructor
class ClassMetaBuilder {

    private final ByteBuddy byteBuddy;

    <T> ClassMeta build(Binding<T> binding, List<AspectMeta> aspectMetas) throws InjectException {
        ClassMeta.ClassMetaBuilder builder = ClassMeta.builder().bindClass(getBindingClass(binding));

        if (binding.getContract().getType().getParameterized() != null) {
            L.log("build with params: " + binding.getContract().getType().getTheClass());
            buildParameterized(binding.getContract().getType(), builder, aspectMetas);
        } else if (aspectMetas.size() == 0) {
            L.log("simple build: " + binding.getContract().getType().getTheClass());
            builder
                    .buildClass(binding.getContract().getType().getClazz().getTheClass())
                    .contractClass(binding.getContract().getType().getClazz().getTheClass());
        } else {
            L.log("build with aspects: " + binding.getContract().getType().getTheClass());
            buildWithAspects(binding.getContract().getType(), builder, aspectMetas);
        }
        return builder.build();
    }

    private Class<?> getBindingClass(Binding<?> binding) {
        if (binding.getType().getParameterized() != null) {
            return binding.getType().getParameterized().getRawType();
        }
        return binding.getType().getClazz().getTheClass();
    }

    private void buildWithAspects(Type<?> type, ClassMeta.ClassMetaBuilder metaBuilder, List<AspectMeta> aspectMetas) {
        DynamicType.Builder<?> builder = byteBuddy.subclass(type.getClazz().getTheClass());
        applyAspects(builder, aspectMetas);

        Class<?> builderClass = builder.make().load(getClass().getClassLoader()).getLoaded();

        metaBuilder.buildClass(builderClass);
        metaBuilder.contractClass(builderClass.getSuperclass());
    }

    private void buildParameterized(Type<?> type, ClassMeta.ClassMetaBuilder metaBuilder, List<AspectMeta> aspectMetas) {
        DynamicType.Builder<?> builder = applyAspects(byteBuddy.subclass(type.getParameterized().getType()), aspectMetas);

        Class<?> builderClass = builder.make().load(type.getParameterized().getRawType().getClassLoader()).getLoaded();

        metaBuilder.buildClass(builderClass);
        metaBuilder.contractClass(builderClass.getSuperclass());
    }

    private DynamicType.Builder<?> applyAspects(DynamicType.Builder<?> builder, List<AspectMeta> aspectMetas) {
        L.log(" : " + aspectMetas);
        for (AspectMeta aspectMeta : aspectMetas) {

            boolean result = ElementMatchers.isAnnotatedWith(
                    RequiredArgsConstructor.class
            ).matches(
                    TypeDescription.ForLoadedType.of(
                            RequiredArgsConstructor.class
                    )
            );

            L.log(aspectMeta.getAnnotationClass() + " :LOOP: " + result);

            builder = builder
                    .method
                            (not(isDeclaredBy(Object.class)))
                            //(ElementMatchers.isAnnotatedWith(aspectMeta.getAnnotationClass()))
                    .intercept(
                            InvocationHandlerAdapter.of(aspectMeta.getInvocationHandler())
                    );
        }
        return builder;
    }
}
