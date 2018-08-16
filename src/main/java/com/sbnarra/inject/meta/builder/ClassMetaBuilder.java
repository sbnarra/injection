package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.AspectMeta;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.List;

@RequiredArgsConstructor
class ClassMetaBuilder {

    private final ByteBuddy byteBuddy;

    <T> ClassMeta build(Binding<T> binding, List<AspectMeta> aspectMetas) throws InjectException {
        ClassMeta.ClassMetaBuilder builder = ClassMeta.builder().bindClass(getBindingClass(binding));

        if (binding.getContract().getType().getParameterized() != null) {
            buildParameterized(binding.getContract().getType(), builder, aspectMetas);
        } else if (aspectMetas.size() == 0) {
            builder
                    .buildClass(binding.getContract().getType().getClazz().getTheClass())
                    .contractClass(binding.getContract().getType().getClazz().getTheClass());
        } else {
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
        DynamicType.Builder<?> builder = byteBuddy.subclass(type.getParameterized().getType());
        applyAspects(builder, aspectMetas);

        Class<?> builderClass = builder.make().load(getClass().getClassLoader()).getLoaded();

        metaBuilder.buildClass(builderClass);
        metaBuilder.contractClass(builderClass.getSuperclass());
    }

    private void applyAspects(DynamicType.Builder<?> builder, List<AspectMeta> aspectMetas) {
        aspectMetas.stream().forEach(aspectMeta ->
                builder
                        .method(
                                ElementMatchers.anyOf(aspectMeta.getMethods()))
                        .intercept(
                                InvocationHandlerAdapter.of(aspectMeta.getInvocationHandler())));
    }
}
