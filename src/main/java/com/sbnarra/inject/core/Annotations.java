package com.sbnarra.inject.core;

import lombok.Getter;
import lombok.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Getter
public class Annotations {
    private static final Class<?> RAW_INJECT_CLASS = Inject.class;
    @SuppressWarnings("unchecked")
    private static final Class<Annotation> INJECT_CLASS = (Class<Annotation>) RAW_INJECT_CLASS;

    public static Named getName(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (Named.class.isInstance(annotation)) {
                return Named.class.cast(annotation);
            }
        }
        return null;
    }

    public static <T extends AnnotatedElement> List<T> findInject(T... annotatedElements) {
        return findList(annotatedElements, (f, l) -> l.add(f.getExecutable()), INJECT_CLASS);
    }

    public static <T extends AnnotatedElement> List<Integer> findInjectIndexes(T... executables) {
        return findList(executables, (f, l) -> l.add(f.getIndex()), INJECT_CLASS);
    }

    private static <R, T extends AnnotatedElement> List<R> findList(
            T[] annotatedElements, BiConsumer<AnnotatedElementResult<T>, List<R>> mapper, Class<Annotation>... annotationClasses) {
        List<R> found = new ArrayList<>();
        for (int i = 0; i < annotatedElements.length; i++) {
            for (Class<Annotation> annotationClass : annotationClasses) {
                AnnotatedElementResult<T> foundAnnotations = findAnnotation(null, i, annotatedElements[i], annotationClass);
                if (foundAnnotations != null) {
                    mapper.accept(foundAnnotations, found);
                }
            }
        }
        return found;
    }

    private static <T extends AnnotatedElement> AnnotatedElementResult<T> findAnnotations(T annotatedElement, int index, List<Class<Annotation>> annotationClasses) {
        AnnotatedElementResult<T> result = null;
        for (Class<Annotation> annotationClass : annotationClasses) {
            result = findAnnotation(result, index, annotatedElement, annotationClass);
        }
        return result;
    }

    private static <T extends AnnotatedElement> AnnotatedElementResult<T> findAnnotation(
            AnnotatedElementResult<T> result, int index, T annotatedElement, Class<Annotation> annotationClass) {
        Annotation annotation = annotatedElement.getAnnotation(annotationClass);
        if (annotation != null) {
            if (result == null) {
                result = new AnnotatedElementResult<>(annotatedElement, index);
            }
            result.getAnnotations().add(annotation);
        }
        return result;
    }

    @Value
    private static class AnnotatedElementResult<T extends AnnotatedElement> {
        private final T executable;
        private final Integer index;
        private final List<Annotation> annotations = new ArrayList<>();
    }
}
