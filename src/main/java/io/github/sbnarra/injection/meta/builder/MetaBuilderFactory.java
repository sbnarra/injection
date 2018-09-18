package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.annotation.Annotations;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

@RequiredArgsConstructor
public class MetaBuilderFactory {
    private final Annotations annotations;

    public MetaBuilder newInstance() {
        return createMetaBuilder(new ByteBuddy().with(AnnotationRetention.ENABLED), getClass().getClassLoader());
    }

    private MetaBuilder createMetaBuilder(ByteBuddy byteBuddy, ClassLoader classLoader) {
        InjectBuilder injectBuilder = new InjectBuilder(annotations);

        ClassBuilder classBuilder = new ClassBuilder(byteBuddy, injectBuilder, classLoader);

        ParametersMetaBuilder parametersMetaBuilder = new ParametersMetaBuilder(injectBuilder, annotations);
        ConstructorBuilder constructorBuilder = new ConstructorBuilder(parametersMetaBuilder, annotations);

        FieldBuilder fieldBuilder = new FieldBuilder(parametersMetaBuilder, annotations);
        MethodBuilder methodBuilder = new MethodBuilder(parametersMetaBuilder, annotations);
        MembersBuilder membersBuilder = new MembersBuilder(methodBuilder, fieldBuilder);

        AspectBuilder aspectBuilder = new AspectBuilder();
        return new MetaBuilder(classBuilder, constructorBuilder, membersBuilder, aspectBuilder);
    }

}
