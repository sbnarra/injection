package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.context.scope.ScopeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class ScopeContract extends Contract<ScopeBinding, ScopeContract, ScopeBinding> {
    private final ScopeHandler scopeHandler;

    public ScopeContract(ScopeBinding scopeBinding, ScopeHandler scopeHandler) {
        super(scopeBinding);
        this.scopeHandler = scopeHandler;
    }
}
