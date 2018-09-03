package com.sbnarra.inject.core;

import com.sbnarra.inject.ThreadLocal;
import lombok.Getter;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Annotations {
    private static final String JAVAX_QUALIFIER = "javax.inject.Qualifier";
    private static final String JAVAX_SCOPE = "javax.inject.Scope";

    private final List<Class<Annotation>> qualifier = new ArrayList<>();
    private final List<Class<Annotation>> scope = new ArrayList<>();

    public Annotations() {
        registerScope(Singleton.class);
        registerScope(ThreadLocal.class);
    }

    public static Annotations newInstance() throws AnnotationsException {
        return new Annotations()
                .registerQualifier(getAnnotation(JAVAX_QUALIFIER))
                .registerScope(getAnnotation(JAVAX_SCOPE));
    }

    public Annotations registerQualifier(Class<?> annotationClass) {
        qualifier.add((Class<Annotation>) annotationClass);
        return this;
    }

    public Named getName(Annotation[] annotations) throws AnnotationsException {
        for (Annotation annotation : annotations) {
            if (Named.class.isInstance(annotation)) {
                return Named.class.cast(annotation);
            }
        }
        return null;
    }

    public Annotations registerScope(Class<?> annotationClass) {
        scope.add((Class<Annotation>) annotationClass);
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
