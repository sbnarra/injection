package io.github.sbnarra.injection.registry;

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

    protected CONTRACT setContract(CONTRACT contract) throws RegistryException {
        this.contract = contract;
        register();
        return this.contract;
    }

    protected void register() throws RegistryException {
        register(registryBindings);
    }

    protected abstract void register(Collection<RAW_BINDING> registryBindings) throws RegistryException;

    public CONTRACT getContract() {
        return this.contract;
    }
}
