package com.sbnarra.inject.registry;

import com.sbnarra.inject.scope.ScopeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScopeBinding extends Binding<ScopeContract, ScopeBinding> {
    private final Class<?> scoped;

    public ScopeBinding(Class<?> scoped, Collection<ScopeBinding> registryBindings) {
        super(registryBindings);
        this.scoped = scoped;
    }

    public ScopeContract with(ScopeHandler scopeHandler) {
        return setContract(new ScopeContract(this, scopeHandler));
    }

    @Override
    protected void register(Collection registryBindings) {
        registryBindings.add(this);
    }
}
