package com.sbnarra.inject.registry;

import com.sbnarra.inject.scope.ScopeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class ScopeContract extends Contract<ScopeBinding> {
    private final ScopeHandler scopeHandler;

    public ScopeContract(ScopeBinding scopeBinding, ScopeHandler scopeHandler) {
        super(scopeBinding);
        this.scopeHandler = scopeHandler;
    }
}
