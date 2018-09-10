package io.github.sbnarra.injection.scope;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.meta.Meta;

public interface ScopeHandler {
    void destoryScope() throws ScopeHandlerException;
    <T> T get(Meta<T> meta, Injector injector) throws ScopeHandlerException;
}
