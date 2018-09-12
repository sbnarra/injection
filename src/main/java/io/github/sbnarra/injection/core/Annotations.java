package io.github.sbnarra.injection.core;

import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
public class Annotations {
    private static final Class<?> RAW_INJECT_CLASS = Inject.class;
    private static final Class<Annotation> INJECT_CLASS = (Class<Annotation>) RAW_INJECT_CLASS;

    private static final Class<?> RAW_QUALIFIER_CLASS = Qualifier.class;
    private static final Class<Annotation> QUALIFIER_CLASS = (Class<Annotation>) RAW_QUALIFIER_CLASS;

    public static <T extends AnnotatedElement> boolean shouldInject(T annotatedElement) {
        return Stream.of(annotatedElement.getDeclaredAnnotations()).anyMatch(a -> Inject.class.isInstance(a));
    }

    public static <T extends AnnotatedElement> List<T> findInject(T... annotatedElements) {
        List<T> injectAnnotatedElements = new ArrayList<>();
        for (T annotatedElement : annotatedElements) {
            if (Stream.of(annotatedElement.getDeclaredAnnotations()).anyMatch(a -> Inject.class.isInstance(a))) {
                injectAnnotatedElements.add(annotatedElement);
            }
        }
        return injectAnnotatedElements;
    }

    public static <T extends AnnotatedElement> List<Integer> findInjectIndexes(T... annotatedElements) {
        List<Integer> injectAnnotatedElements = new ArrayList<>();
        for (int i = 0; i < annotatedElements.length; i++) {
            if (annotatedElements[i].getDeclaredAnnotation(Inject.class) != null) {
                injectAnnotatedElements.add(i);
            }
        }
        return injectAnnotatedElements;
    }

    public static <T extends AnnotatedElement> Annotation findScope(T annotatedElement) throws AnnotationsException {
        return findAnnotation(annotatedElement, Scope.class);
    }

    public static <T extends AnnotatedElement> Annotation findQualifier(T annotatedElement) throws AnnotationsException {
        return findAnnotation(annotatedElement, Qualifier.class);
    }

    private static <T extends AnnotatedElement> Annotation findAnnotation(T annotatedElement, Class<? extends Annotation> annotationClass) throws AnnotationsException {
        List<Annotation> annotatedElements = new ArrayList<>();
        for (Annotation annotation : annotatedElement.getDeclaredAnnotations()) {
            if (annotation.annotationType().getDeclaredAnnotation(annotationClass) != null) {
                annotatedElements.add(annotation);
            }
        }

        if (annotatedElements.isEmpty()) {
            return null;
        } else if (annotatedElements.size() > 1) {
            throw new AnnotationsException("multiple qualifiers found: " + annotatedElement + ": " + annotatedElements);
        }
        return annotatedElements.get(0);
    }
}
