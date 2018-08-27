package com.sbnarra.inject.registry;

import com.sbnarra.inject.meta.Scoped;
import com.sbnarra.inject.scope.ScopeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScopeBinding extends Binding<ScopeContract> {
    private final Scoped scoped;

    public ScopeContract with(ScopeHandler scopeHandler) {
        return setContract(new ScopeContract(this, scopeHandler));
    }
}
