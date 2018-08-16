package com.sbnarra.inject.registry;

import com.sbnarra.inject.InjectException;
import lombok.Setter;

import java.lang.annotation.Annotation;

@Setter
public abstract class Registration {

    private Registry registry;

    public abstract void register() throws InjectException;

    protected <T> Binding<T> bind(Class<T> tClass) {
        return registry.bind(tClass);
    }

    protected <T> Binding<T> bind(Type<T> type) {
        return registry.bind(type);
    }

    protected AnnotationBinding intercept(Class<?> annotationClass) {
        return registry.intercept(annotationClass);
    }
}
