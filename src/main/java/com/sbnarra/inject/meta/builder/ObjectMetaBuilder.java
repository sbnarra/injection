package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.L;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.AspectMeta;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObjectMetaBuilder {

    private final ClassMetaBuilder classMetaBuilder;
    private final ConstructorMetaBuilder constructorMetaBuilder;
    private final MethodMetaBuilder methodMetaBuilder;
    private final FieldMetaBuilder fieldMetaBuilder;
    private final AspectMetaBuilder aspectMetaBuilder;

    public ObjectMeta build(Binding<?> binding, Graph graph, Registry registry) throws InjectException {
        List<AspectMeta> aspectMetas = aspectMetaBuilder.build(binding.getType().getTheClass(), registry.getAnnotationBindings());
        ClassMeta classMeta = classMetaBuilder.build(binding, aspectMetas);
        return ObjectMeta.builder()
                .classMeta(classMeta)
                .constructorMeta(constructorMetaBuilder.build(classMeta, graph, registry))

                .fieldMeta(fieldMetaBuilder.build(classMeta, graph, registry))
                .methodMeta(methodMetaBuilder.build(classMeta, graph, registry))

                .aspectMeta(aspectMetas)

                .named(binding.getNamed())
                .isSingleton(binding.getContract().isAsSingleton())

                .build();
    }
}
