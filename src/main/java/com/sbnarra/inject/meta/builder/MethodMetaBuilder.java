package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.InjectionAnnotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.MethodMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Registry;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class MethodMetaBuilder {
    private final InjectionAnnotations injectionAnnotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<MethodMeta> build(ClassMeta classMeta, Graph graph, Registry registry) throws InjectException {
        Class<?> bClass = classMeta.getContractClass();
        List<MethodMeta> methodMetas = new ArrayList<>();
        for (Method method : bClass.getDeclaredMethods()) {
            for (Class<Annotation> annotationClass : injectionAnnotations.injectAnnotations()) {
                if (method.getAnnotation(annotationClass) != null) {

                    methodMetas.add(MethodMeta.builder()
                            .method(method)
                            .fields(parametersMetaBuilder.getParameters(method, graph, registry))
                            .build());
                }
            }
        }
        return methodMetas;
    }
}
