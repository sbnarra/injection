package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.TypeBinding;

import java.util.List;

public class MetaBuilder extends AbstractBuilder {

    private final ClassBuilder classBuilder;
    private final ConstructorBuilder constructorBuilder;
    private final MethodBuilder methodBuilder;
    private final FieldBuilder fieldBuilder;
    private final AspectBuilder aspectBuilder;

    public MetaBuilder(Annotations annotations, ClassBuilder classBuilder, ConstructorBuilder constructorBuilder,
                       MethodBuilder methodBuilder, FieldBuilder fieldBuilder, AspectBuilder aspectBuilder) {
        super(annotations);
        this.classBuilder = classBuilder;
        this.constructorBuilder = constructorBuilder;
        this.methodBuilder = methodBuilder;
        this.fieldBuilder = fieldBuilder;
        this.aspectBuilder = aspectBuilder;
    }

    public <T> Meta<T> build(TypeBinding<T> binding, Context context) throws BuilderException {
        Meta.MetaBuilder<T> builder = Meta.builder();
        Meta.Class<T> classMeta;

        try {
            if (binding.getInstance() != null) {
                classMeta = classBuilder.build(binding);
                builder.instance(binding.getInstance());
            } else {
                Type<?> type = binding.getType();
                List<Meta.Aspect> aspectMetas = aspectBuilder.build(type.getTheClass(), context.getRegistry().getInterceptionBindings());
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
