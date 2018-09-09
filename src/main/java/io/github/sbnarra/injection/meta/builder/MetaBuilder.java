package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.TypeBinding;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MetaBuilder {

    private final ClassBuilder classBuilder;
    private final ConstructorBuilder constructorBuilder;
    private final MethodBuilder methodBuilder;
    private final FieldBuilder fieldBuilder;
    private final AspectBuilder aspectBuilder;

    public <T> Meta<T> build(TypeBinding<T> binding, Context context) throws BuilderException {
        Meta.MetaBuilder<T> builder = Meta.builder();
        Meta.Class<T> classMeta;

        try {
            if (binding.getInstance() != null) {
                classMeta = classBuilder.build(binding);
                builder.instance(binding.getInstance());
            } else {
                Type<?> type = binding.getType();
                List<Meta.Aspect> aspectMetas = aspectBuilder.build(type.getTheClass(), context.registry().getInterceptionBindings());
                classMeta = classBuilder.build(binding, aspectMetas);
                builder.constructor(constructorBuilder.build(classMeta, context))
                        .field(fieldBuilder.build(classMeta, context))
                        .method(methodBuilder.build(classMeta, context))
                        .aspect(aspectMetas);
            }
        } catch (BuilderException e) {
            throw new BuilderException("failed to build: " + binding, e);
        }

        builder.clazz(classMeta);
        return builder.build();
    }
}