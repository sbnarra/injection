package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.core.Annotations;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

public class MetaBuilderFactory {
    public MetaBuilder newInstance(Annotations annotations) {
        return createMetaBuilder(new ByteBuddy().with(AnnotationRetention.ENABLED), annotations);
    }

    private MetaBuilder createMetaBuilder(ByteBuddy byteBuddy, Annotations annotations) {
        InjectBuilder injectBuilder = createInjectBuilder(annotations);
        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder(annotations, injectBuilder);
        ClassBuilder classBuilder = createClassBuilder(byteBuddy, injectBuilder);
        ConstructorBuilder constructorBuilder = createConstructorBuilder(annotations, parametersMetaBuilder);
        MethodBuilder methodBuilder = createMethodBuilder(annotations, parametersMetaBuilder);
        FieldBuilder fieldBuilder = createFieldBuilder(annotations, injectBuilder);
        AspectBuilder aspectBuilder = createAspectBuilder();
        return new MetaBuilder(annotations, classBuilder, constructorBuilder, methodBuilder, fieldBuilder, aspectBuilder);
    }

    private InjectBuilder createInjectBuilder(Annotations annotations) {
        return new InjectBuilder(annotations);
    }

    private ClassBuilder createClassBuilder(ByteBuddy byteBuddy, InjectBuilder injectBuilder) {
        return new ClassBuilder(byteBuddy, injectBuilder);
    }

    private ConstructorBuilder createConstructorBuilder(Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new ConstructorBuilder(annotations, parametersMetaBuilder);
    }

    private MethodBuilder createMethodBuilder(Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new MethodBuilder(annotations, parametersMetaBuilder);
    }

    private FieldBuilder createFieldBuilder(Annotations annotations, InjectBuilder injectBuilder) {
        return new FieldBuilder(annotations, injectBuilder);
    }

    private AspectBuilder createAspectBuilder() {
        return new AspectBuilder();
    }
}
