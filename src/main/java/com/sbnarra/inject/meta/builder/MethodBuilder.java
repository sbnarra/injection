package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class MethodBuilder {
    private final Annotations annotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Method> build(Meta.Class classMeta, Context context) throws BuilderException {
        Class bClass = classMeta.getContractClass();
        List<Meta.Method> methodMetas = new ArrayList<>();
        for (java.lang.reflect.Method method : bClass.getDeclaredMethods()) {
            for (Class annotationClass : annotations.getInject()) {
                if (method.getAnnotation(annotationClass) != null) {
                    method.setAccessible(true);
                    methodMetas.add(Meta.Method.builder()
                            .method(method)
                            .fields(parametersMetaBuilder.getParameters(method, context))
                            .build());
                }
            }
        }
        return methodMetas;
    }
}
