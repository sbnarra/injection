package com.sbnarra.inject.registry;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Scoped;
import lombok.Setter;

import java.lang.annotation.Annotation;

@Setter
public abstract class Registration {

    private Registry registry;

    public abstract void register() throws RegistryException;

    protected <T> TypeBinding<T> bind(Class<T> tClass) {
        return registry.bind(tClass);
    }

    protected <T> TypeBinding<T> bind(Type<T> type) {
        return registry.bind(type);
    }

    protected AnnotationBinding intercept(Class<?> annotationClass) throws RegistryException {
        return registry.intercept(asAnnotation(annotationClass));
    }

    protected ScopeBinding scoped(Scoped scoped) {
        return registry.scoped(scoped);
    }

    private Class<Annotation> asAnnotation(Class<?> annotationClass) throws RegistryException {
            if (!annotationClass.isAnnotation()) {
                throw new RegistryException(annotationClass + " is not an annotation");
            }
        return (Class<Annotation>) annotationClass;
    }
}
