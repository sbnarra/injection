package com.sbnarra.inject.registry;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.core.Type;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@ToString
public class Registry {

    private final List<AnnotationBinding> interceptionBindings = new ArrayList<>();
    private final Collection<TypeBinding<?>> typeBindings = new ArrayList<>();
    private final List<ScopeBinding> scopeBindings = new ArrayList<>();

    Registry() {
    }

    public <T> TypeBinding<T> bind(Class<T> tClass) {
        return bind(new Type<T>(tClass) {});
    }

    public <T> TypeBinding<T> bind(Type<T> type) {
        return new TypeBinding<>(type, typeBindings);
    }

    public ScopeBinding scoped(Class<?> scoped) {
        return new ScopeBinding(scoped, scopeBindings);
    }

    public AnnotationBinding intercept(Class<Annotation> annotationClass) {
        return new AnnotationBinding(annotationClass, interceptionBindings);
    }

    public TypeBinding<?> find(Type<?> type, Annotation qualifier) {
        return find(type.getTheClass(), qualifier);
    }

    public TypeBinding<?> find(Class<?> aClass, Annotation qualifier) {
        for (TypeBinding<?> typeBinding : typeBindings) {
            Debug.log(typeBinding);
            if (qualifier != null) {
                if (typeBinding.getQualifier() == null || !qualifier.annotationType().equals(typeBinding.getQualifier().annotationType())) {
                    continue;
                }
            }

            Type<?> type = typeBinding.getType();
            if (type.getTheClass().equals(aClass)) {
                return typeBinding;
            }
        }
        return null;
    }
}
