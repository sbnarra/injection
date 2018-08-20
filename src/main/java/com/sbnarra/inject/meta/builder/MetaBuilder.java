package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.registry.Registry;
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

    public <T> Meta<T> build(TypeBinding<?> binding, Graph graph, Registry registry) throws InjectException {
        List<Meta.Aspect> aspectMetas = aspectBuilder.build(binding.getType().getTheClass(), registry.getInterceptionBindings());
        Meta.Class classMeta = classBuilder.build(binding, aspectMetas);
        return Meta.<T>builder()
                .clazz(classMeta)
                .constructor(constructorBuilder.build(classMeta, graph, registry))

                .field(fieldBuilder.build(classMeta, graph, registry))
                .method(methodBuilder.build(classMeta, graph, registry))

                .aspect(aspectMetas)

                .qualifier(getQualifier(binding, classMeta))
                .scoped(binding.getContract().getScopeAnnotation())

                .build();
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
