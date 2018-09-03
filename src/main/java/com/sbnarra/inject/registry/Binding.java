package com.sbnarra.inject.registry;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class Binding<C extends Contract, B extends Binding<?, ?>> {
    private C contract;
    @ToString.Exclude private final Collection<B> registryBindings;

    protected C setContract(C contract) {
        register();
        return this.contract = contract;
    }

    protected void register() {
        register(registryBindings);
    }

    protected abstract void register(Collection<B> registryBindings);

    public C getContract() {
        return this.contract;
    }
}
