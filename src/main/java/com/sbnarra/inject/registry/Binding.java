package com.sbnarra.inject.registry;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class Binding<
        CONTRACT extends Contract<RAW_BINDING, CONTRACT, ACTUAL_BINDING>,
        RAW_BINDING extends Binding<?, ?, ?>,
        ACTUAL_BINDING extends Binding<CONTRACT, RAW_BINDING, ACTUAL_BINDING>> {
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Collection<RAW_BINDING> registryBindings;
    private CONTRACT contract;

    protected CONTRACT setContract(CONTRACT contract) {
        register();
        return this.contract = contract;
    }

    protected void register() {
        register(registryBindings);
    }

    protected abstract void register(Collection<RAW_BINDING> registryBindings);

    public CONTRACT getContract() {
        return this.contract;
    }
}
