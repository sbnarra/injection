package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectionAnnotations;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;

import net.bytebuddy.implementation.attribute.AnnotationRetention;

@RequiredArgsConstructor
public class ObjectMetaBuilderFactory {

    private final InjectionAnnotations injectionAnnotations;

    public ObjectMetaBuilder get() {
        ByteBuddy byteBuddy = new ByteBuddy().with(AnnotationRetention.ENABLED);
        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder(injectionAnnotations);
        return objectMetaBuilder(byteBuddy, injectionAnnotations, parametersMetaBuilder);
    }

    private ObjectMetaBuilder objectMetaBuilder(ByteBuddy byteBuddy, InjectionAnnotations injectionAnnotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new ObjectMetaBuilder(
                classMetaBuilder(byteBuddy),
                constructorMetaBuilder(injectionAnnotations, parametersMetaBuilder),
                methodMetaBuilder(injectionAnnotations, parametersMetaBuilder),
                fieldMetaBuilder(injectionAnnotations, parametersMetaBuilder),
                aspectMetaBuilder()
        );
    }

    public ClassMetaBuilder classMetaBuilder(ByteBuddy byteBuddy) {
        return new ClassMetaBuilder(byteBuddy);
    }

    public ConstructorMetaBuilder constructorMetaBuilder(InjectionAnnotations injectionAnnotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new ConstructorMetaBuilder(injectionAnnotations, parametersMetaBuilder);
    }

    public MethodMetaBuilder methodMetaBuilder(InjectionAnnotations injectionAnnotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new MethodMetaBuilder(injectionAnnotations, parametersMetaBuilder);
    }

    public FieldMetaBuilder fieldMetaBuilder(InjectionAnnotations injectionAnnotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new FieldMetaBuilder(injectionAnnotations, parametersMetaBuilder);
    }

    public AspectMetaBuilder aspectMetaBuilder() {
        return new AspectMetaBuilder();
    }
}
