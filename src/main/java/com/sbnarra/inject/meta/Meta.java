package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.lang.annotation.Annotation;
import java.util.List;

@Value
@Builder
public class Meta<T> {

    private final T instance;

    private final Class<T> clazz;
    private final Constructor<T> constructor;
    private final List<Field> field;
    private final List<Method> method;
    private final List<Aspect> aspect;

    @Value
    @Builder
    public static class Inject {
        private final Annotation scoped;
        private final Annotation qualifier;
    }

    @Value
    @Builder
    public static class Class<T> {
        @NonNull private final Inject inject;
        private final java.lang.Class<T> buildClass;
        private final java.lang.Class<?> contractClass;
        private final java.lang.Class<?> bindClass;
    }

    @Value
    @Builder
    public static class Constructor<T> {
        private final java.lang.reflect.Constructor<T> constructor;
        private final List<Parameter> parameters;
    }

    @Value
    @Builder
    public static class Method {
        private final java.lang.reflect.Method method;
        private final List<Parameter> parameters;
    }

    @Value
    @Builder
    public static class Parameter {
        private final boolean useProvider;
        private final Inject inject;
        private final Meta<?> meta;
    }

    @Value
    @Builder
    public static class Field {
        private final boolean useProvider;
        private final java.lang.reflect.Field field;
        private final Inject inject;
        private final Meta<?> meta;
    }

    @Value
    @Builder
    public static class Aspect {
        private final java.lang.Class<? extends Annotation> annotationClass;
        private final com.sbnarra.inject.aspect.Aspect aspect;
    }
}
