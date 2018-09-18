package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.type.Type;
import lombok.Setter;

public abstract class Registration {

    @Setter private Registry registry;

    public abstract void register() throws RegistryException;

    protected <T> TypeBinding<T> bind(Class<T> tClass) {
        return registry.bind(tClass);
    }

    protected <T> TypeBinding<T> bind(Type<T> type) {
        return registry.bind(type);
    }

    protected AspectBinding intercept(Class<?> annotationClass) throws RegistryException {
        return registry.intercept(annotationClass);
    }

    protected ScopeBinding scoped(Class<?> scoped) {
        return registry.scoped(scoped);
    }

}
