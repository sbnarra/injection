package com.sbnarra.inject.registry;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class Binding<C extends Contract<GB>, GB extends Binding<?, ?>> {
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Collection<GB> registryBindings;
    private C contract;

    protected C setContract(C contract) {
        register();
        return this.contract = contract;
    }

    protected void register() {
        register(registryBindings);
    }

    protected abstract void register(Collection<GB> registryBindings);

    public C getContract() {
        return this.contract;
    }
}
