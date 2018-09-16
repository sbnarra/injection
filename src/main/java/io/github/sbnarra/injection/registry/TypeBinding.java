package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.annotation.Named;
import io.github.sbnarra.injection.annotation.SimpleAnnotation;
import io.github.sbnarra.injection.type.Type;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class TypeBinding<T> extends Binding<TypeContract<T>, TypeBinding<?>, TypeBinding<T>> {
    private final Type<T> type;
    private T instance;
    private Annotation qualifier;

    public TypeBinding(Type<T> type, Collection<TypeBinding<?>> registryBindings) {
        super(registryBindings);
        this.type = type;
    }

    public void to(T instance) throws RegistryException {
        this.instance = instance;
        register();
    }

    public TypeContract<T> with(Type<? extends T> type) throws RegistryException {
        return setContract(new TypeContract<>(this, type));
    }

    public TypeContract<T> with(Class<? extends T> aClass) throws RegistryException {
        return setContract(new TypeContract(this, new Type<T>(aClass) {}));
    }

    public TypeBinding<T> named(String named) {
        return qualified(new Named(named));
    }

    public TypeBinding<T> qualified(Class<?> qualifier) {
        SimpleAnnotation simpleAnnotation = new SimpleAnnotation((Class<? extends Annotation>) qualifier);
        return qualified(simpleAnnotation);
    }

    public TypeBinding<T> qualified(Annotation qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    @Override
    protected void register(Collection<TypeBinding<?>> registryBindings) throws RegistryException {
        if (instance == null && getContract() == null) {
            throw new RegistryException(type + ": missing instance/type contract");
        }
        registryBindings.add(this);
    }

    public TypeContract<T> asSingleton() throws RegistryException {
        TypeContract<T> contract = with(type);
        contract.asSingleton();
        return contract;
    }
}
