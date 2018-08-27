package com.sbnarra.inject.registry;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Qualifier;
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

    public static Registry registrate(Registration... registrations) throws RegistryException {
        return registrate(Arrays.asList(registrations));
    }

    public static Registry registrate(List<Registration> registrations) throws RegistryException {
        Registry registry = new Registry();
        for (Registration registration : registrations) {
            registration.setRegistry(registry);
            registration.register();
        }
        new RegistryValidator().validate(registry);
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


    public TypeBinding<?> find(Type<?> type, Qualifier qualifier) {
        return find(type.getTheClass(), qualifier);
    }

    public TypeBinding<?> find(Class<?> aClass, Qualifier qualifier) {
        for (TypeBinding<?> typeBinding : typeBindings) {
            if (qualifier != null && !qualifier.equals(typeBinding.getQualifier())) {
                continue;
            }

            Type<?> type = typeBinding.getType();
            if (type.getTheClass().equals(aClass)) {
                return typeBinding;
            }
        }
        return null;
    }
}
