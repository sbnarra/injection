package com.sbnarra.inject;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Scoped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class TypeContract<T> extends Contract<TypeBinding<T>> {
    private Class<?> scopeAnnotation;

    private final Type<? extends T> type;
    private final T instance;

    public TypeContract(TypeBinding<T> typeBinding, Type<? extends T> aType) {
        this(typeBinding, aType, null);
    }

    public TypeContract(TypeBinding<T> typeBinding, T instance) {
        this(typeBinding, null, instance);
    }

    private TypeContract(TypeBinding<T> binding, Type<? extends T> type, T instance) {
        super(binding);
        this.type = type;
        this.instance = instance;
    }

    public void asSingleton() {
        scopedAs(Scoped.Singleton.class);
    }

    public void asThreadLocal() {
        scopedAs(Scoped.ThreadLocal.class);
    }

    public void scopedAs(Class<?> scopeAnnotation) {
        this.scopeAnnotation = scopeAnnotation;
    }
}
