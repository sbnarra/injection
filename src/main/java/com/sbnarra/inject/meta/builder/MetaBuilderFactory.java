package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.core.Annotations;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

public class MetaBuilderFactory {
    public MetaBuilder newInstance(Annotations annotations) {
        return newInstance(annotations, new ByteBuddy().with(AnnotationRetention.ENABLED));
    }

    public MetaBuilder newInstance(Annotations annotations, ByteBuddy byteBuddy) {
        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder(annotations);
        return createMetaBuilder(byteBuddy, annotations, parametersMetaBuilder);
    }

    private MetaBuilder createMetaBuilder(ByteBuddy byteBuddy, Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new MetaBuilder(
                createClassBuilder(byteBuddy),
                createConstructorBuilder(annotations, parametersMetaBuilder),
                createMethodBuilder(annotations, parametersMetaBuilder),
                createFieldBuilder(annotations, parametersMetaBuilder),
                createAspectBuilder());
    }

    private ClassBuilder createClassBuilder(ByteBuddy byteBuddy) {
        return new ClassBuilder(byteBuddy);
    }

    private ConstructorBuilder createConstructorBuilder(Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new ConstructorBuilder(annotations, parametersMetaBuilder);
    }

    private MethodBuilder createMethodBuilder(Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new MethodBuilder(annotations, parametersMetaBuilder);
    }

    private FieldBuilder createFieldBuilder(Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        return new FieldBuilder(annotations, parametersMetaBuilder);
    }

    private AspectBuilder createAspectBuilder() {
        return new AspectBuilder();
    }
}
