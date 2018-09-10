package io.github.sbnarra.injection.meta.builder;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

public class MetaBuilderFactory {
    public MetaBuilder newInstance() {
        return createMetaBuilder(new ByteBuddy().with(AnnotationRetention.ENABLED));
    }

    private MetaBuilder createMetaBuilder(ByteBuddy byteBuddy) {
        InjectBuilder injectBuilder = createInjectBuilder();
        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder(injectBuilder);
        ClassBuilder classBuilder = createClassBuilder(byteBuddy, injectBuilder);
        ConstructorBuilder constructorBuilder = createConstructorBuilder(parametersMetaBuilder);
        MethodBuilder methodBuilder = createMethodBuilder(parametersMetaBuilder);
        FieldBuilder fieldBuilder = createFieldBuilder(parametersMetaBuilder);
        AspectBuilder aspectBuilder = createAspectBuilder();
        return new MetaBuilder(classBuilder, constructorBuilder, methodBuilder, fieldBuilder, aspectBuilder);
    }

    private InjectBuilder createInjectBuilder() {
        return new InjectBuilder();
    }

    private ClassBuilder createClassBuilder(ByteBuddy byteBuddy, InjectBuilder injectBuilder) {
        return new ClassBuilder(byteBuddy, injectBuilder);
    }

    private ConstructorBuilder createConstructorBuilder(ParametersMetaBuilder parametersMetaBuilder) {
        return new ConstructorBuilder(parametersMetaBuilder);
    }

    private MethodBuilder createMethodBuilder(ParametersMetaBuilder parametersMetaBuilder) {
        return new MethodBuilder(parametersMetaBuilder);
    }

    private FieldBuilder createFieldBuilder(ParametersMetaBuilder parametersMetaBuilder) {
        return new FieldBuilder(parametersMetaBuilder);
    }

    private AspectBuilder createAspectBuilder() {
        return new AspectBuilder();
    }
}
