package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.L;
import com.sbnarra.inject.meta.AspectMeta;
import com.sbnarra.inject.registry.AnnotationBinding;
import com.sbnarra.inject.registry.InterceptionContract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class AspectMetaBuilder {

    List<AspectMeta> build(Class<?> tClass, List<AnnotationBinding> annotationBindings) {
        List<AspectMeta> aspectMetas = new ArrayList<>();
        for (AnnotationBinding annotationBinding : annotationBindings) {
            AspectMeta aspectMeta = buildAspectMeta(tClass, annotationBinding);
            if (aspectMeta != null) {
                aspectMetas.add(aspectMeta);
            }
        }
        return aspectMetas;
    }

    private AspectMeta buildAspectMeta(Class<?> tClass, AnnotationBinding annotationBinding) {
        for (Method method : tClass.getDeclaredMethods()) {
            if (interceptMethod(method, annotationBinding)) {
                    InterceptionContract interceptionContract = annotationBinding.getInterceptionContract();
                    return AspectMeta.builder()
                            .annotationClass(annotationBinding.getAnnotationClass())
                            .invocationHandler(interceptionContract.getInvocationHandler())
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
