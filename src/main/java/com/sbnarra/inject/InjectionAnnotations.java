package com.sbnarra.inject;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class InjectionAnnotations {
    private static final String JAVAX_INJECT = "javax.inject.Inject";
    private static final String JAVAX_QUALIFIER = "javax.inject.Qualifier";
    private static final String JAVAX_NAMED = "javax.inject.Named";
    private static final String JAVAX_SCOPE = "javax.inject.Scope";
    private static final String JAVAX_SINGLETON = "javax.inject.Singleton";

    private final List<Class<Annotation>> inject = new ArrayList<>();
    private final List<Class<Annotation>> qualifier = new ArrayList<>();
    private final List<Class<Annotation>> named = new ArrayList<>();
    private final List<Class<Annotation>> scope = new ArrayList<>();
    private final List<Class<Annotation>> singleton = new ArrayList<>();

    private InjectionAnnotations newInstance() throws InjectException {
        return new InjectionAnnotations()
                .registerInject(getAnnotation(JAVAX_INJECT))
                .registerQualifier(getAnnotation(JAVAX_QUALIFIER))
                .registerNamed(getAnnotation(JAVAX_NAMED))
                .registerScope(getAnnotation(JAVAX_SCOPE))
                .registerSingleton(getAnnotation(JAVAX_SINGLETON));
    }

    private boolean isAnnotation(Annotation annotation, List<Class<Annotation>> annotationClasses) {
        for (Class<Annotation> annotationClass : annotationClasses) {
            if (annotationClass.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    public InjectionAnnotations registerInject(Class<Annotation> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public boolean isInject(Annotation annotation) {
        return isAnnotation(annotation, inject);
    }

    public List<Class<Annotation>> injectAnnotations() {
        return inject;
    }

    public InjectionAnnotations registerQualifier(Class<Annotation> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public boolean isQualifier(Annotation annotation) {
        return isAnnotation(annotation, qualifier);
    }

    public InjectionAnnotations registerNamed(Class<Annotation> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public boolean isNamed(Annotation annotation) {
        return isAnnotation(annotation, named);
    }

    public InjectionAnnotations registerScope(Class<Annotation> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public boolean isScope(Annotation annotation) {
        return isAnnotation(annotation, scope);
    }

    public InjectionAnnotations registerSingleton(Class<Annotation> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public boolean isSingleton(Annotation annotation) {
        return isAnnotation(annotation, singleton);
    }

    private Class<Annotation> getAnnotation(String name) throws InjectException {
        Class<?> theClass;
        try {
            theClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new InjectException(name + ": not found", e);
        }

        if (theClass.isAnnotation()) {
            return (Class<Annotation>) theClass;
        }
        throw new InjectException(theClass  + " is not an annotation");
    }
}
