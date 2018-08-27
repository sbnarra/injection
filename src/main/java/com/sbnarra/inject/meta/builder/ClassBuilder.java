package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.aspect.Aspect;
import com.sbnarra.inject.aspect.AspectInvoker;
import com.sbnarra.inject.aspect.Invoker;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.TypeBinding;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;

import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;

@RequiredArgsConstructor
class ClassBuilder {
    private final ByteBuddy byteBuddy;

    <T> Meta.Class<T> build(TypeBinding<T> typeBinding) {
        return Meta.Class.<T>builder()
                .contractClass(typeBinding.getInstance().getClass())
                .build();
    }

    <T> Meta.Class<T> build(TypeBinding<T> typeBinding, List<Meta.Aspect> aspectMetas) {
        Meta.Class.ClassBuilder builder = Meta.Class.builder().bindClass(typeBinding.getType().getTheClass());

        if (typeBinding.getContract().getType().isParameterized()) {
            parameterizedBuild(typeBinding.getContract().getType(), builder, aspectMetas);
        } else if (aspectMetas.size() == 0) {
            builder
                    .buildClass(typeBinding.getContract().getType().getTheClass())
                    .contractClass(typeBinding.getContract().getType().getTheClass());
        } else {
            buildWithAspects(typeBinding.getContract().getType(), builder, aspectMetas);
        }
        return builder.build();
    }

    private void parameterizedBuild(Type<?> type, Meta.Class.ClassBuilder metaBuilder, List<Meta.Aspect> aspectMetas) {
        DynamicType.Builder<?> builder = applyAspects(byteBuddy.subclass(type.getParameterized().getType()), aspectMetas);
        Class builderClass = builder.make().load(type.getTheClass().getClassLoader()).getLoaded();

        metaBuilder.buildClass(builderClass);
        metaBuilder.contractClass(builderClass.getSuperclass());
    }

    private void buildWithAspects(Type<?> type, Meta.Class.ClassBuilder metaBuilder, List<Meta.Aspect> aspectMetas) {
        DynamicType.Builder<?> builder = byteBuddy.subclass(type.getTheClass());

        Class builderClass = applyAspects(builder, aspectMetas).make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        metaBuilder.buildClass(builderClass);
        metaBuilder.contractClass(builderClass.getSuperclass());
    }

    private DynamicType.Builder<?> applyAspects(DynamicType.Builder<?> builder, List<Meta.Aspect> aspectMetas) {
        for (Meta.Aspect aspectMeta : aspectMetas) {
            builder = builder.method(isAnnotatedWith(aspectMeta.getAnnotationClass()))
                    .intercept(aspectDelegation(aspectMeta.getAspect()));
        }
        return builder;
    }

    private MethodDelegation aspectDelegation(Aspect aspect) {
        return MethodDelegation.withDefaultConfiguration()
                .withBinders(Morph.Binder.install(Invoker.class))
                .to(new AspectInvoker(aspect));
    }
}
