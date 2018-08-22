package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

import java.lang.annotation.Annotation;
import java.util.List;

@Value
@Builder
public class Meta<T> {
    private final java.lang.Class<?> scoped;
    private final Qualifier qualifier;

    private final T instance;

    private final Class<T> clazz;
    private final Constructor constructor;
    private final List<Field> field;
    private final List<Method> method;
    private final List<Aspect> aspect;

    @Value
    @Builder
    public static class Class<T> {
        private final java.lang.Class<T> buildClass;
        private final java.lang.Class<?> contractClass;
        private final java.lang.Class<?> bindClass;
    }

    @Value
    @Builder
    public static class Constructor<T> {
        private final java.lang.reflect.Constructor<T> constructor;
        private final List<Meta> fields;
    }

    @Value
    @Builder
    public static class Field {
        private final java.lang.reflect.Field field;
        private final Meta meta;
    }

    @Value
    @Builder
    public static class Method {
        private final java.lang.reflect.Method method;
        private final List<Meta> fields;
    }

    @Value
    @Builder
    public static class Aspect {
        private final java.lang.Class<? extends Annotation> annotationClass;
        private final com.sbnarra.inject.aspect.Aspect aspect;
    }
}
