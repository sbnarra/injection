package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.annotation.Annotations;
import io.github.sbnarra.injection.type.Type;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
public class Registry {

    @Getter private final List<AspectBinding> interceptionBindings = new ArrayList<>();
    @Getter private final Collection<TypeBinding<?>> typeBindings = new ArrayList<>();
    @Getter private final List<ScopeBinding> scopeBindings = new ArrayList<>();
    @ToString.Exclude private final Annotations annotations;

    public <T> TypeBinding<T> bind(Class<T> tClass) {
        return bind(new Type<T>(tClass) {});
    }

    public <T> TypeBinding<T> bind(Type<T> type) {
        return new TypeBinding<>(type, typeBindings);
    }

    public ScopeBinding scoped(Class<?> scoped) {
        return new ScopeBinding(annotations, scoped, scopeBindings);
    }

    public AspectBinding intercept(Class<?> annotationClass) {
        return new AspectBinding(annotationClass, interceptionBindings);
    }

    public TypeBinding<?> find(Type<?> type, Annotation qualifier) {
        return find(type.getTheClass(), qualifier);
    }

    public TypeBinding<?> find(Class<?> aClass, Annotation qualifier) {
        for (TypeBinding<?> typeBinding : typeBindings) {
            if (qualifier != null) {
                if (typeBinding.getQualifier() == null || !typeBinding.getQualifier().equals(qualifier)) {
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
