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
    private final Predicate<? super AnnotatedElement> hasDeclaredInjectAnnotation;
    private final Predicate<? super AnnotatedElement> hasDeclaredScopeAnnotation;
    private final Predicate<? super Annotation> annotationAnnotatedWithScope;
    private final Predicate<? super Annotation> annotationAnnotatedWithQualifier;

    public Annotations(Class<?> injectClass, Class<?> scopeClass, Class<?> qualifierClass) {
        this(isClass(injectClass), isClass(scopeClass), isClass(qualifierClass));
    }

    public Annotations(List<Class<?>> injectClass, List<Class<?>> scopeClass, List<Class<?>> qualifierClass) {
        this(isClass(injectClass), isClass(scopeClass), isClass(qualifierClass));
    }

    private Annotations(Predicate<? super Annotation> isInject, Predicate<? super Annotation> isScope, Predicate<? super Annotation> isQualifier) {
        this.hasDeclaredInjectAnnotation = checkDeclaredAnnotations(isInject);
        this.hasDeclaredScopeAnnotation = checkDeclaredAnnotations(isScope);
        this.annotationAnnotatedWithScope = testAnnotation(hasDeclaredScopeAnnotation);
        this.annotationAnnotatedWithQualifier = testAnnotation(checkDeclaredAnnotations(isQualifier));
    }

    public static Annotations newDefault() {
        return new Annotations(Inject.class, Scope.class, Qualifier.class);
    }

    /*
     * inject helper methods
     */

    public <T extends AnnotatedElement> boolean hasInjectAnnotation(T annotatedElement) {
        return hasDeclaredInjectAnnotation.test(annotatedElement);
    }

    public <T extends AnnotatedElement> List<T> findAnnotatedElementsWithInjectAnnotation(T[] annotatedElements) {
        return findAllMatching(hasDeclaredInjectAnnotation, annotatedElements);
    }

    public <T extends AnnotatedElement> List<Integer> findIndexesOfAnnotatedElementsWithInjectAnnotation(T[] elements) {
        return IntStream.range(0, elements.length).filter(i -> hasInjectAnnotation(elements[i])).boxed().collect(Collectors.toList());
    }

    /*
     * scope helper methods
     */

    public boolean hasScopeAnnotation(Class<?> theClass) {
        return hasDeclaredScopeAnnotation.test(theClass);
    }

    public Annotation findScopeAnnotation(Annotation[] annotations) {
        return findFirstMatching(annotationAnnotatedWithScope, annotations);
    }

    public Annotation findScopeAnnotation(AnnotatedElement annotatedElement) {
        return findScopeAnnotation(annotatedElement.getDeclaredAnnotations());
    }

    /*
     * qualifier helper methods
     */

    public <T extends AnnotatedElement> Annotation findQualifierAnnotation(T annotatedElement) {
        return findQualifierAnnotation(annotatedElement.getDeclaredAnnotations());
    }

    public Annotation findQualifierAnnotation(Annotation[] annotations) {
        return findFirstMatching(annotationAnnotatedWithQualifier, annotations);
    }

    /*
     * generic helper methods
     */

    private static <T> List<T> findAllMatching(Predicate<? super T> condition, T[] items) {
        return Arrays.stream(items).filter(condition).collect(Collectors.toList());
    }

    private static <T> T findFirstMatching(Predicate<? super T> condition, T[] items) {
        return Arrays.stream(items).filter(condition).findFirst().orElse(null);
    }

    private static Predicate<? super Annotation> testAnnotation(Predicate<? super AnnotatedElement> annotatedElementAnnotatedWith) {
        return annotation -> annotatedElementAnnotatedWith.test(annotation.annotationType());
    }

    private static Predicate<? super AnnotatedElement> checkDeclaredAnnotations(Predicate<? super Annotation> annotationCheck) {
        return annotatedElement -> Arrays.stream(annotatedElement.getDeclaredAnnotations()).anyMatch(annotationCheck);
    }

    private static Predicate<? super Annotation> isClass(List<Class<?>> theClasses) {
        List<Predicate<? super Annotation>> isOneOfTheClasses = theClasses.stream().map(Annotations::isClass).collect(Collectors.toList());
        return annotation -> isOneOfTheClasses.stream().anyMatch(isOneOfTheClass -> isOneOfTheClass.test(annotation));
    }

    private static Predicate<? super Annotation> isClass(Class<?> theClass) {
        return annotation -> annotation.annotationType().equals(theClass);
    }
}
