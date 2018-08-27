package com.sbnarra.inject.core;

import com.sbnarra.inject.meta.Scoped;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Data
public class Annotations {
    private static final String JAVAX_INJECT = "javax.inject.Inject";
    private static final String JAVAX_QUALIFIER = "javax.inject.Qualifier";
    private static final String JAVAX_NAMED = "javax.inject.Named";
    private static final String JAVAX_SCOPE = "javax.inject.Scope";
    private static final String JAVAX_SINGLETON = "javax.inject.Singleton";

    private final List<Class<?>> inject = new ArrayList<>();
    private final List<Class<?>> qualifier = new ArrayList<>();
    private final List<Class<?>> named = new ArrayList<>();
    private final List<Class<?>> scope = new ArrayList<>();
    private final List<Class<?>> singleton = new ArrayList<>();
    private final List<Class<?>> threadLocal = new ArrayList<>();

    public Annotations() {
        registerSingleton(Scoped.Singleton.class);
        registerThreadLocal(Scoped.ThreadLocal.class);
    }

    public static Annotations newInstance() throws AnnotationsException {
        return new Annotations()
                .registerInject(getAnnotation(JAVAX_INJECT))
                .registerQualifier(getAnnotation(JAVAX_QUALIFIER))
                .registerNamed(getAnnotation(JAVAX_NAMED))
                .registerScope(getAnnotation(JAVAX_SCOPE))
                .registerSingleton(getAnnotation(JAVAX_SINGLETON));
    }

    public Annotations registerInject(Class<?> annotationClass) {
        inject.add(annotationClass);
        return this;
    }

    public Annotations registerQualifier(Class<?> annotationClass) {
        qualifier.add(annotationClass);
        return this;
    }

    public Annotations registerNamed(Class<?> annotationClass) {
        named.add(annotationClass);
        return this;
    }

    public String getName(Annotation[] annotations) throws AnnotationsException {
        for (Annotation annotation : annotations) {
            for (Class<?> annotationClass : named) {
                if (annotationClass.isInstance(annotation)) {
                    Object returned = null;
                    try {
                        returned = annotationClass.getDeclaredMethod("value").invoke(annotation);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new AnnotationsException("error getting named value", e);
                    }
                    if (returned != null) {
                        return returned.toString();
                    }
                }
            }
        }
        return null;
    }

    public Annotations registerScope(Class<?> annotationClass) {
        scope.add(annotationClass);
        return this;
    }

    public Annotations registerSingleton(Class<?> annotationClass) {
        singleton.add(annotationClass);
        return this;
    }

    public Annotations registerThreadLocal(Class<?> annotationClass) {
        threadLocal.add(annotationClass);
        return this;
    }

    private static Class<?> getAnnotation(String name) throws AnnotationsException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new AnnotationsException(name + ": not found", e);
        }
    }
}
