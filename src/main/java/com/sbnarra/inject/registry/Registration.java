package com.sbnarra.inject.registry;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Scoped;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Setter
public abstract class Registration {

    private Registry registry;

    public abstract void register() throws InjectException;

    protected <T> TypeBinding<T> bind(Class<T> tClass) {
        return registry.bind(tClass);
    }

    protected <T> TypeBinding<T> bind(Type<T> type) {
        return registry.bind(type);
    }

    protected AnnotationBinding intercept(Class<?> annotationClass) throws InjectException {
        return registry.intercept(asAnnotation(annotationClass));
    }

    protected ScopeBinding scoped(Scoped scoped) throws InjectException {
        return registry.scoped(scoped);
    }

    private List<Class<Annotation>> asAnnotation(Class<?>... annotationClasses) throws InjectException {
        List<Class<Annotation>> annotations = new ArrayList<>();
        for (Class<?> annotationClass : annotationClasses) {
            annotations.add(asAnnotation(annotationClass));
        }
        return annotations;
    }

    private Class<Annotation> asAnnotation(Class<?> annotationClass) throws InjectException {
            if (!annotationClass.isAnnotation()) {
                throw new InjectException(annotationClass + " is not an annotation");
            }
        return (Class<Annotation>) annotationClass;
    }
}
