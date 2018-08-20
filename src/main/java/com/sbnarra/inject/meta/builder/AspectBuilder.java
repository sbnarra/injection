package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.AnnotationBinding;
import com.sbnarra.inject.registry.AnnotationContract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class AspectBuilder {

    List<Meta.Aspect> build(Class<?> tClass, List<AnnotationBinding> annotationBindings) {
        List<Meta.Aspect> aspectMetas = new ArrayList<>();
        for (AnnotationBinding annotationBinding : annotationBindings) {
            Meta.Aspect aspectMeta = buildAspectMeta(tClass, annotationBinding);
            if (aspectMeta != null) {
                aspectMetas.add(aspectMeta);
            }
        }
        return aspectMetas;
    }

    private Meta.Aspect buildAspectMeta(Class<?> tClass, AnnotationBinding annotationBinding) {
        for (Method method : tClass.getDeclaredMethods()) {
            if (interceptMethod(method, annotationBinding)) {
                    AnnotationContract interceptionContract = annotationBinding.getInterceptionContract();
                    return Meta.Aspect.builder()
                            .annotationClass(annotationBinding.getAnnotationClass())
                            .aspect(interceptionContract.getAspect())
                            .build();
            }
        }
        return null;
    }

    private boolean interceptMethod(Method method, AnnotationBinding annotationBinding) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotationBinding.getAnnotationClass().isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }
}
