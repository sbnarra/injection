package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.ThreadLocal;
import io.github.sbnarra.injection.core.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class TypeContract<T> extends Contract<TypeBinding<?>, TypeContract<T>, TypeBinding<T>> {
    private Annotation scoped;

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
        scopedAs(() -> Singleton.class);
    }

    public void asThreadLocal() {
        scopedAs(() -> ThreadLocal.class);
    }

    public void scopedAs(Class<?> scopeAnnotation) throws RegistryException {
        if (!scopeAnnotation.isAnnotation()) {
            throw new RegistryException(scopeAnnotation + ": is not annotation");
        }
        scopedAs(() -> (Class<? extends Annotation>) scopeAnnotation);
    }

    public void scopedAs(Annotation scopeAnnotation) {
        this.scoped = scopeAnnotation;
    }
}
