package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Context;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.registry.TypeBinding;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class MetaBuilder {

    private final ClassBuilder classBuilder;
    private final ConstructorBuilder constructorBuilder;
    private final MethodBuilder methodBuilder;
    private final FieldBuilder fieldBuilder;
    private final AspectBuilder aspectBuilder;

    public <T> Meta<T> build(TypeBinding<?> binding, Context context) throws InjectException {
        try {
            List<Meta.Aspect> aspectMetas = aspectBuilder.build(binding.getType().getTheClass(), context.getRegistry().getInterceptionBindings());
            Meta.Class classMeta = classBuilder.build(binding, aspectMetas);
            return Meta.<T>builder()
                    .clazz(classMeta)
                    .constructor(constructorBuilder.build(classMeta, context))

                    .field(fieldBuilder.build(classMeta, context))
                    .method(methodBuilder.build(classMeta, context))

                    .aspect(aspectMetas)

                    .qualifier(getQualifier(binding, classMeta))
                    .scoped(binding.getContract().getScopeAnnotation())

                    .build();
        } catch (InjectException e) {
            throw new InjectException("failed to build: " + binding, e);
        }
    }

    private Qualifier getQualifier(TypeBinding<?> binding, Meta.Class classMeta) {
        if (binding.getQualifier() != null) {
            return binding.getQualifier();
        }

        // Todo - complete implementation
        classMeta.getContractClass().getAnnotations();

        return null;
    }

    private Qualifier getScope(TypeBinding<?> binding, Meta.Class classMeta) {
        if (binding.getQualifier() != null) {
            return binding.getQualifier();
        }

        // Todo - complete implementation
        classMeta.getContractClass().getAnnotations();

        return null;
    }
}
