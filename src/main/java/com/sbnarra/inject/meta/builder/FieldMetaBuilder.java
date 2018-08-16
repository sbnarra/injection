package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.InjectionAnnotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Registry;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
@RequiredArgsConstructor
class FieldMetaBuilder {

    private final InjectionAnnotations injectionAnnotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<ObjectMeta> build(ClassMeta classMeta, Graph graph, Registry registry) throws InjectException {
        Class<?> bClass = classMeta.getContractClass();
        List<ObjectMeta> objectMetas = new ArrayList<>();
        INJECT_FIELD: for (Field field : bClass.getDeclaredFields()) {

            for (Class<Annotation> injectAnnotationClass : injectionAnnotations.injectAnnotations()) {
                if (field.getAnnotation(injectAnnotationClass) != null) {
                    String named = injectionAnnotations.getName(field.getDeclaredAnnotations());
                    ObjectMeta objectMeta = parametersMetaBuilder.getParameter(field.getDeclaringClass(), named, graph, registry);
                    objectMetas.add(objectMeta);
                    continue INJECT_FIELD;
                }
            }
        }
        return objectMetas;
    }
}
