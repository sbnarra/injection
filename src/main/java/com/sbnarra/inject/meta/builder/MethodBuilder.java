package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.Registry;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class MethodBuilder {
    private final Annotations annotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Method> build(Meta.Class classMeta, Graph graph, Registry registry) throws InjectException {
        Class bClass = classMeta.getContractClass();
        List<Meta.Method> methodMetas = new ArrayList<>();
        for (java.lang.reflect.Method method : bClass.getDeclaredMethods()) {
            for (Class annotationClass : annotations.getInject()) {
                if (method.getAnnotation(annotationClass) != null) {
                    methodMetas.add(Meta.Method.builder()
                            .method(method)
                            .fields(parametersMetaBuilder.getParameters(method, graph, registry))
                            .build());
                }
            }
        }
        return methodMetas;
    }
}
