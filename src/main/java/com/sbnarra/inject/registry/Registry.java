package com.sbnarra.inject.registry;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Scoped;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@ToString
public class Registry {

    private final List<AnnotationBinding> interceptionBindings = new ArrayList<>();
    private final List<TypeBinding<?>> typeBindings = new ArrayList<>();
    private final List<ScopeBinding> scopeBindings = new ArrayList<>();

    private Registry() {
    }

    public static Registry registrate(Registration... registrations) throws InjectException {
        return registrate(Arrays.asList(registrations));
    }

    public static Registry registrate(List<Registration> registrations) throws InjectException {
        Registry registry = new Registry();
        for (Registration registration : registrations) {
            registration.setRegistry(registry);
            registration.register();
        }
        return registry;
    }

    public <T> TypeBinding<T> bind(Class<T> tClass) {
        TypeBinding<T> typeBinding = new TypeBinding<>(tClass);
        typeBindings.add(typeBinding);
        return typeBinding;
    }

    public <T> TypeBinding<T> bind(Type<T> type) {
        TypeBinding<T> typeBinding = new TypeBinding<>(type);
        typeBindings.add(typeBinding);
        return typeBinding;
    }

    public ScopeBinding scoped(Scoped scoped) {
        ScopeBinding scopeBinding = new ScopeBinding(scoped);
        scopeBindings.add(scopeBinding);
        return scopeBinding;
    }

    public AnnotationBinding intercept(Class<Annotation> annotationClass) {
        AnnotationBinding annotationBinding = new AnnotationBinding(annotationClass);
        interceptionBindings.add(annotationBinding);
        return annotationBinding;
    }

    public TypeBinding<?> find(Class<?> aClass) {
        for (TypeBinding<?> typeBinding : typeBindings) {
            Type<?> type = typeBinding.getType();
            if (type.getParameterized() != null && type.getParameterized().getRawType().equals(aClass)) {
                return typeBinding;
            } else if (type.getTheClass().equals(aClass)) {
                return typeBinding;
            }
        }
        return null;
    }
}
