package io.github.sbnarra.injection.context.scope;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.meta.Meta;

public interface ScopeHandler {
    void destoryScope() throws ContextException;
    <T> T get(Meta<T> meta, Meta.Inject inject, Injector injector) throws ContextException;
}
