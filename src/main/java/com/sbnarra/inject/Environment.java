package com.sbnarra.inject;

import sun.reflect.annotation.AnnotatedTypeFactory;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class Environment {
    private static final String JAVAX_INJECT = "javax.inject.Inject";
    private final List<Class<Annotation>> inject = new ArrayList<>();

    private static final String JAVAX_QUALIFIER = "javax.inject.Qualifier";
    private final List<Class<Annotation>> qualifier = new ArrayList<>();
    private static final String JAVAX_NAMED = "javax.inject.Named";
    private final List<Class<Annotation>> named = new ArrayList<>();

    private static final String JAVAX_SCOPE = "javax.inject.Scope";
    private final List<Class<Annotation>> scope = new ArrayList<>();
    private static final String JAVAX_SINGLETON = "javax.inject.Singleton";
    private final List<Class<Annotation>> singleton = new ArrayList<>();

    private Environment newInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        inject.add(getAnnotation(JAVAX_INJECT));
        qualifier.add(getAnnotation(JAVAX_QUALIFIER));
        named.add(getAnnotation(JAVAX_NAMED));
        scope.add(getAnnotation(JAVAX_SCOPE));
        singleton.add(getAnnotation(JAVAX_SINGLETON));
    }

    private Class<Annotation> getAnnotation(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> theClass = Class.forName(name);
        if (theClass.isAnnotation()) {
            return (Class<Annotation>) theClass;
        }
        throw new Class
    }
}
