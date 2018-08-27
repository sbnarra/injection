package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.TypeBinding;
import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
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
        try {
            Meta.MetaBuilder<T> builder = Meta.builder();
            Meta.Class<T> classMeta;
            if (binding.getInstance() != null) {
                classMeta = classBuilder.build(binding);
                builder.instance(binding.getInstance());
            } else {
                List<Meta.Aspect> aspectMetas = aspectBuilder.build(binding.getType().getTheClass(), context.getRegistry().getInterceptionBindings());
                classMeta = classBuilder.build(binding, aspectMetas);
                builder.constructor(constructorBuilder.build(classMeta, context))
                        .field(fieldBuilder.build(classMeta, context))
                        .method(methodBuilder.build(classMeta, context))
                        .aspect(aspectMetas);
            }
            builder.clazz(classMeta)
                    .qualifier(getQualifier(binding, classMeta))
                    .scoped(getScope(binding, classMeta));
            return builder.build();
        } catch (BuilderException e) {
            throw new BuilderException("failed to build: " + binding, e);
        }
    }

    private Qualifier getQualifier(TypeBinding<?> binding, Meta.Class classMeta) {
        if (binding.getQualifier() != null) {
            return binding.getQualifier();
        }

        // Todo - complete implementation, scan call annotations
        classMeta.getContractClass().getAnnotations();

        return null;
    }

    private Class<?> getScope(TypeBinding<?> binding, Meta.Class classMeta) {
        if (binding.getContract() == null) {
            return null;
        }

        if (binding.getContract().getScopeAnnotation() != null) {
            return binding.getContract().getScopeAnnotation();
        }

        // Todo - complete implementation, scan call annotations
        classMeta.getContractClass().getAnnotations();

        return null;
    }
}
