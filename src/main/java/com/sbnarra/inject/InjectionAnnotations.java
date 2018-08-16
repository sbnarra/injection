package com.sbnarra.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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

    public static InjectionAnnotations newInstance() throws InjectException {
        return new InjectionAnnotations()
                .registerInject(getAnnotation(JAVAX_INJECT))
                .registerQualifier(getAnnotation(JAVAX_QUALIFIER))
                .registerNamed(getAnnotation(JAVAX_NAMED))
                .registerScope(getAnnotation(JAVAX_SCOPE))
                .registerSingleton(getAnnotation(JAVAX_SINGLETON));
    }

    public InjectionAnnotations registerInject(Class<Annotation> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public List<Class<Annotation>> injectAnnotations() {
        return inject;
    }

    public InjectionAnnotations registerQualifier(Class<Annotation> annotationClass) {
        qualifier.add(annotationClass);
        return this;
    }

    public InjectionAnnotations registerNamed(Class<Annotation> annotationClass) {
        named.add(annotationClass);
        return this;
    }

    public String getName(Annotation[] annotations) throws InjectException {
        for (Annotation annotation : annotations) {
            for (Class<Annotation> annotationClass : named) {
                if (annotationClass.isInstance(annotation)) {
                    Object returned = null;
                    try {
                        returned = annotationClass.getDeclaredMethod("value").invoke(annotation);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new InjectException("error getting named value", e);
                    }
                    if (returned != null) {
                        return returned.toString();
                    }
                }
            }
        }
        return null;
    }

    public InjectionAnnotations registerScope(Class<Annotation> annotationClass) {
        scope.add(annotationClass);
        return this;
    }

    public InjectionAnnotations registerSingleton(Class<Annotation> annotationClass) {
        singleton.add(annotationClass);
        return this;
    }

    private static Class<Annotation> getAnnotation(String name) throws InjectException {
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
