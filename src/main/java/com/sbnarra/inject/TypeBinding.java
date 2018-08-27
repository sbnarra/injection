package com.sbnarra.inject;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Qualifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class TypeBinding<T> extends Binding<TypeContract<T>> {
    private final Type<T> type;
    private T instance;
    private Qualifier qualifier;

    public TypeBinding(Type<T> type) {
        this.type = type;
    }

    public TypeBinding(Class<T> aClass) {
        this(new Type<T>(aClass) {});
    }

    public void to(T instance) {
        this.instance = instance;
    }

    public TypeContract<T> with(Type<? extends T> type) {
        return setContract(new TypeContract<>(this, type));
    }

    public TypeContract<T> with(Class<? extends T> aClass) {
        return setContract(new TypeContract<>(this, new Type<T>(aClass) {}));
    }

    public TypeBinding<T> named(String named) {
        return qualified(new Qualifier.Named(named));
    }

    public TypeBinding<T> qualified(Class<?> qualifierAnnotation) {
        return qualified(new Qualifier.Annotated(qualifierAnnotation));
    }

    public TypeBinding<T> qualified(Qualifier qualifier) {
        this.qualifier = qualifier;
        return this;
    }
}
