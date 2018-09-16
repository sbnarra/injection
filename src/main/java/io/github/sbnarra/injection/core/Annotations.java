package io.github.sbnarra.injection.core;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Annotations {
    private static final Predicate<? super Annotation> isAnnotationInject = isAnnotation(Inject.class);
    private static final Predicate<? super AnnotatedElement> annotatedElementAnnotatedWithInject = annotatedElementAnnotatedWith(isAnnotationInject);
    private static final Predicate<? super AnnotatedElement> declaresInject = annotatedElementAnnotatedWithInject::test;

    private static final Predicate<? super Annotation> isAnnotationScope = isAnnotation(Scope.class);
    private static final Predicate<? super AnnotatedElement> annotatedElementAnnotatedWithScope = annotatedElementAnnotatedWith(isAnnotationScope);
    private static final Predicate<? super Annotation> annotationAnnotatedWithScope = annotationAnnotatedWith(annotatedElementAnnotatedWithScope);

    private static final Predicate<? super Annotation> isAnnotationQualifier = isAnnotation(Qualifier.class);
    private static final Predicate<? super AnnotatedElement> annotatedElementAnnotatedWithQualifier = annotatedElementAnnotatedWith(isAnnotationQualifier);
    private static final Predicate<? super Annotation> annotationAnnotatedWithQualifier = annotationAnnotatedWith(annotatedElementAnnotatedWithQualifier);

    private Annotations() {
    }

    /*
     * inject helper methods
     */

    public static <T extends AnnotatedElement> boolean shouldInject(T annotatedElement) {
        return declaresInject.test(annotatedElement);
    }

    public static <T extends AnnotatedElement> List<T> findInject(T[] annotatedElements) {
        return Arrays.stream(annotatedElements).filter(declaresInject).collect(Collectors.toList());
    }

    public static <T extends AnnotatedElement> List<Integer> findInjectIndexes(T[] annotatedElements) {
        return IntStream.range(0, annotatedElements.length).filter(i -> shouldInject(annotatedElements[i])).boxed().collect(Collectors.toList());
    }

    /*
     * scope helper methods
     */

    public static boolean hasScopeAnnotation(Class<?> theClass) {
        return annotatedElementAnnotatedWithScope.test(theClass);
    }

    public static Annotation findScopeAnnotation(Annotation[] annotations) {
        return findAnnotatedAnnotation(annotations, annotationAnnotatedWithScope);
    }

    public static <T extends AnnotatedElement> Annotation findScope(T annotatedElement) {
        return findScopeAnnotation(annotatedElement.getDeclaredAnnotations());
    }

    /*
     * qualifier helper methods
     */

    public static <T extends AnnotatedElement> Annotation findQualifier(T annotatedElement) {
        return findAnnotatedAnnotation(annotatedElement.getDeclaredAnnotations(), annotationAnnotatedWithQualifier);
    }

    public static Annotation findQualifierAnnotation(Annotation[] annotations) {
        return findAnnotatedAnnotation(annotations, annotationAnnotatedWithQualifier);
    }

    /*
     * generic helper methods
     */

    private static Annotation findAnnotatedAnnotation(Annotation[] annotations, Predicate<? super Annotation> annotationAnnotatedWith) {
        return Arrays.stream(annotations).filter(annotationAnnotatedWith).findFirst().orElse(null);
    }

    private static Predicate<? super Annotation> annotationAnnotatedWith(Predicate<? super AnnotatedElement> annotatedElementAnnotatedWith) {
        return annotation -> annotatedElementAnnotatedWith.test(annotation.annotationType());
    }

    private static Predicate<? super AnnotatedElement> annotatedElementAnnotatedWith(Predicate<? super Annotation> isAnnotation) {
        return annotatedElement -> Arrays.stream(annotatedElement.getDeclaredAnnotations()).anyMatch(isAnnotation);
    }

    private static Predicate<? super Annotation> isAnnotation(Class<?> annotationClass) {
        return annotation -> annotation.annotationType().equals(annotationClass);
    }
}
