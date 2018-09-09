package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.AspectBinding;
import io.github.sbnarra.injection.registry.AspectContract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class AspectBuilder {

    List<Meta.Aspect> build(Class<?> tClass, List<AspectBinding> aspectBindings) {
        List<Meta.Aspect> aspectMetas = new ArrayList<>();
        for (AspectBinding aspectBinding : aspectBindings) {
            Meta.Aspect aspectMeta = buildAspectMeta(tClass, aspectBinding);
            if (aspectMeta != null) {
                aspectMetas.add(aspectMeta);
            }
        }
        return aspectMetas;
    }

    private Meta.Aspect buildAspectMeta(Class<?> tClass, AspectBinding aspectBinding) {
        for (Method method : tClass.getDeclaredMethods()) {
            if (interceptMethod(method, aspectBinding)) {
                    AspectContract interceptionContract = aspectBinding.getContract();
                    return Meta.Aspect.builder()
                            .annotationClass((Class<? extends Annotation>) aspectBinding.getAnnotationClass())
                            .aspect(interceptionContract.getAspect())
                            .build();
            }
        }
        return null;
    }

    private boolean interceptMethod(Method method, AspectBinding aspectBinding) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (aspectBinding.getAnnotationClass().isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }
}
