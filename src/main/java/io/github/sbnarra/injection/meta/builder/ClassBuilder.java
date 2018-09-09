package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.Helper;
import io.github.sbnarra.injection.aspect.Aspect;
import io.github.sbnarra.injection.aspect.AspectInvoker;
import io.github.sbnarra.injection.aspect.Invoker;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.TypeBinding;
import io.github.sbnarra.injection.registry.TypeContract;
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
    private final InjectBuilder injectBuilder;

    <T> Meta.Class<T> build(TypeBinding<T> typeBinding) throws BuilderException {
        Class<?> contractClass = typeBinding.getInstance().getClass();
        return Meta.Class.<T>builder()
                .contractClass(contractClass)
                .inject(injectBuilder.build(contractClass, typeBinding))
                .build();
    }

    <T> Meta.Class<T> build(TypeBinding<T> typeBinding, List<Meta.Aspect> aspectMetas) throws BuilderException {
        Meta.Class.ClassBuilder<T> builder = Meta.Class.<T>builder().bindClass(typeBinding.getType().getTheClass());

        TypeContract<T> contract = typeBinding.getContract();
        Type<? extends T> type = contract.getType();
        try {
            Helper.checkBuildability(type);
        } catch (Helper.HelperException e) {
            throw new BuilderException(e.getMessage());
        }

        Class<?> contractClass;
        if (type.isParameterized()) {
            contractClass = parameterizedBuild(type, builder, aspectMetas);
        } else if (aspectMetas.size() == 0) {
            builder.buildClass(type.getTheClass()).contractClass(contractClass = type.getTheClass());
        } else {
            contractClass = buildWithAspects(type, builder, aspectMetas);
        }

        return builder
                .inject(injectBuilder.build(contractClass, typeBinding))
                .build();
    }

    private Class<?> parameterizedBuild(Type<?> type, Meta.Class.ClassBuilder<?> metaBuilder, List<Meta.Aspect> aspectMetas) {
        DynamicType.Builder<?> builder = applyAspects(byteBuddy.subclass(type.getParameterized().getType()), aspectMetas);
        Class builderClass = builder.make().load(type.getTheClass().getClassLoader()).getLoaded();

        metaBuilder.buildClass(builderClass);
        Class<?> contractClass = builderClass.getSuperclass();
        metaBuilder.contractClass(contractClass);
        return contractClass;
    }

    private <T> Class<?> buildWithAspects(Type<? extends T> type, Meta.Class.ClassBuilder<T> metaBuilder, List<Meta.Aspect> aspectMetas) {
        DynamicType.Builder<? extends T> builder = byteBuddy.subclass(type.getTheClass());

        Class<? extends T> builderClass = applyAspects(builder, aspectMetas).make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        metaBuilder.buildClass((Class<T>) builderClass);
        Class<?> contractClass = builderClass.getSuperclass();
        metaBuilder.contractClass(contractClass);
        return contractClass;
    }

    private <T> DynamicType.Builder<T> applyAspects(DynamicType.Builder<T> builder, List<Meta.Aspect> aspectMetas) {
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
