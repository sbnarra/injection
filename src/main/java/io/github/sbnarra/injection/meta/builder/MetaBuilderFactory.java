package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.annotation.Annotations;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

public class MetaBuilderFactory {
    public MetaBuilder newInstance(Annotations annotations) {
        return createMetaBuilder(new ByteBuddy().with(AnnotationRetention.ENABLED), annotations);
    }

    private MetaBuilder createMetaBuilder(ByteBuddy byteBuddy, Annotations annotations) {
        InjectBuilder injectBuilder = createInjectBuilder(annotations);
        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder(injectBuilder, annotations);
        ClassBuilder classBuilder = createClassBuilder(byteBuddy, injectBuilder);
        ConstructorBuilder constructorBuilder = createConstructorBuilder(parametersMetaBuilder, annotations);
        MethodBuilder methodBuilder = createMethodBuilder(parametersMetaBuilder, annotations);
        FieldBuilder fieldBuilder = createFieldBuilder(parametersMetaBuilder, annotations);
        MembersBuilder membersBuilder = createMembersBuilder(methodBuilder, fieldBuilder);
        AspectBuilder aspectBuilder = createAspectBuilder();
        return new MetaBuilder(classBuilder, constructorBuilder, membersBuilder, aspectBuilder);
    }

    private MembersBuilder createMembersBuilder(MethodBuilder methodBuilder, FieldBuilder fieldBuilder) {
        return new MembersBuilder(methodBuilder, fieldBuilder);
    }

    private InjectBuilder createInjectBuilder(Annotations annotations) {
        return new InjectBuilder(annotations);
    }

    private ClassBuilder createClassBuilder(ByteBuddy byteBuddy, InjectBuilder injectBuilder) {
        return new ClassBuilder(byteBuddy, injectBuilder);
    }

    private ConstructorBuilder createConstructorBuilder(ParametersMetaBuilder parametersMetaBuilder, Annotations annotations) {
        return new ConstructorBuilder(parametersMetaBuilder, annotations);
    }

    private MethodBuilder createMethodBuilder(ParametersMetaBuilder parametersMetaBuilder, Annotations annotations) {
        return new MethodBuilder(parametersMetaBuilder, annotations);
    }

    private FieldBuilder createFieldBuilder(ParametersMetaBuilder parametersMetaBuilder, Annotations annotations) {
        return new FieldBuilder(parametersMetaBuilder, annotations);
    }

    private AspectBuilder createAspectBuilder() {
        return new AspectBuilder();
    }
}
