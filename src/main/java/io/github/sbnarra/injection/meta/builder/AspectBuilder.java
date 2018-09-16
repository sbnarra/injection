package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.AspectBinding;
import io.github.sbnarra.injection.registry.AspectContract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

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

    private Meta.Aspect buildAspectMeta(Class<?> theClass, AspectBinding aspectBinding) {
        Predicate<Method> interceptMethod = interceptMethod(aspectBinding.getAnnotationClass());
        Method method = Arrays.stream(theClass.getDeclaredMethods()).filter(interceptMethod).findFirst().orElse(null);

        if (method != null) {
            AspectContract interceptionContract = aspectBinding.getContract();
            return Meta.Aspect.builder()
                    .annotationClass((Class<? extends Annotation>) aspectBinding.getAnnotationClass())
                    .aspect(interceptionContract.getAspect())
                    .build();
        }
        return null;
    }

    private Predicate<Method> interceptMethod(Class<?> aspectClass) {
        return method -> Arrays.stream(method.getDeclaredAnnotations()).anyMatch(isInstance(aspectClass));
    }

    private Predicate<Annotation> isInstance(Class<?> aspectClass) {
        return annotation -> aspectClass.isInstance(annotation);
    }
}
