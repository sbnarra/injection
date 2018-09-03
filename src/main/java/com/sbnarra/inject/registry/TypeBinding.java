package com.sbnarra.inject.registry;

import com.sbnarra.inject.core.NamedAnnotation;
import com.sbnarra.inject.core.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class TypeBinding<T> extends Binding<TypeContract<T>, TypeBinding<?>> {
    private final Type<T> type;
    private T instance;
    private Annotation qualifier;

    public TypeBinding(Type<T> type, Collection<TypeBinding<?>> registryBindings) {
        super(registryBindings);
        this.type = type;
    }

    public void to(T instance) {
        register();
        this.instance = instance;
    }

    public TypeContract<T> with(Type<? extends T> type) {
        return setContract(new TypeContract<>(this, type));
    }

    public TypeContract<T> with(Class<? extends T> aClass) {
        return setContract(new TypeContract(this, new Type<T>(aClass) {}));
    }

    public TypeBinding<T> named(String named) {
        return qualified(new NamedAnnotation(named));
    }

    public TypeBinding<T> qualified(Annotation qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    @Override
    protected void register(Collection<TypeBinding<?>> registryBindings) {
        registryBindings.add(this);
    }
}
