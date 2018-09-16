package io.github.sbnarra.injection.annotation;

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
    private static final Predicate<? super Annotation> isInjectAnnotation = annotation -> annotation.annotationType().equals(Inject.class);
    private static final Predicate<? super AnnotatedElement> hasDeclaredInjectAnnotation = checkDeclaredAnnotations(isInjectAnnotation);

    private static final Predicate<? super Annotation> isScopeAnnotation = annotation -> annotation.annotationType().equals(Scope.class);
    private static final Predicate<? super AnnotatedElement> hasDeclaredScopeAnnotation = checkDeclaredAnnotations(isScopeAnnotation);
    private static final Predicate<? super Annotation> annotationAnnotatedWithScope = testAnnotation(hasDeclaredScopeAnnotation);

    private static final Predicate<? super Annotation> isQualifierAnnotation = annotation -> annotation.annotationType().equals(Qualifier.class);
    private static final Predicate<? super AnnotatedElement> hasDeclaredQualifierAnnotation = checkDeclaredAnnotations(isQualifierAnnotation);
    private static final Predicate<? super Annotation> annotationAnnotatedWithQualifier = testAnnotation(hasDeclaredQualifierAnnotation);

    private Annotations() {
    }

    /*
     * inject helper methods
     */

    public static <T extends AnnotatedElement> boolean hasInjectAnnotation(T annotatedElement) {
        return hasDeclaredInjectAnnotation.test(annotatedElement);
    }

    public static <T extends AnnotatedElement> List<T> findAnnotatedElementsWithInjectAnnotation(T[] annotatedElements) {
        return Arrays.stream(annotatedElements).filter(hasDeclaredInjectAnnotation).collect(Collectors.toList());
    }

    public static <T extends AnnotatedElement> List<Integer> findIndexesOfAnnotatedElementsWithInjectAnnotation(T[] annotatedElements) {
        return IntStream.range(0, annotatedElements.length).filter(i -> hasInjectAnnotation(annotatedElements[i])).boxed().collect(Collectors.toList());
    }

    /*
     * scope helper methods
     */

    public static boolean hasScopeAnnotation(Class<?> theClass) {
        return hasDeclaredScopeAnnotation.test(theClass);
    }

    public static Annotation findScopeAnnotation(Annotation[] annotations) {
        return findFirstMatching(annotationAnnotatedWithScope, annotations);
    }

    public static <T extends AnnotatedElement> Annotation findScopeAnnotation(T annotatedElement) {
        return findScopeAnnotation(annotatedElement.getDeclaredAnnotations());
    }

    /*
     * qualifier helper methods
     */

    public static <T extends AnnotatedElement> Annotation findQualifierAnnotation(T annotatedElement) {
        return findQualifierAnnotation(annotatedElement.getDeclaredAnnotations());
    }

    public static Annotation findQualifierAnnotation(Annotation[] annotations) {
        return findFirstMatching(annotationAnnotatedWithQualifier, annotations);
    }

    /*
     * generic helper methods
     */

    private static Annotation findFirstMatching(Predicate<? super Annotation> annotationAnnotatedWith, Annotation[] annotations) {
        return Arrays.stream(annotations).filter(annotationAnnotatedWith).findFirst().orElse(null);
    }

    private static Predicate<? super Annotation> testAnnotation(Predicate<? super AnnotatedElement> annotatedElementAnnotatedWith) {
        return annotation -> annotatedElementAnnotatedWith.test(annotation.annotationType());
    }

    private static Predicate<? super AnnotatedElement> checkDeclaredAnnotations(Predicate<? super Annotation> annotationCheck) {
        return annotatedElement -> Arrays.stream(annotatedElement.getDeclaredAnnotations()).anyMatch(annotationCheck);
    }
}
