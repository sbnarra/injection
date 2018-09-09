package io.github.sbnarra.injection.scope;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.meta.Meta;

public interface ScopeHandler {
    void destoryScope() throws ScopeHandlerException;
    <T> T get(Meta<T> meta, Context context) throws ScopeHandlerException;
}
