package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectionAnnotations;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

public class ObjectMetaBuilderFactory {

    public static ObjectMetaBuilder get(InjectionAnnotations injectionAnnotations) {
        ByteBuddy byteBuddy = new ByteBuddy()
                .with(AnnotationRetention.ENABLED);
        ClassMetaBuilder classMetaBuilder = new ClassMetaBuilder(byteBuddy);

        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder();

        ConstructorMetaBuilder constructorMetaBuilder = new ConstructorMetaBuilder(injectionAnnotations, parametersMetaBuilder);
        MethodMetaBuilder methodMetaBuilder = new MethodMetaBuilder(injectionAnnotations, parametersMetaBuilder);

        FieldMetaBuilder fieldMetaBuilder = new FieldMetaBuilder(injectionAnnotations, parametersMetaBuilder);

        AspectMetaBuilder aspectMetaBuilder = new AspectMetaBuilder();

        return new ObjectMetaBuilder(classMetaBuilder, constructorMetaBuilder, methodMetaBuilder, fieldMetaBuilder, aspectMetaBuilder);
    }
}
