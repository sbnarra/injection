package com.sbnarra.inject.scope;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.meta.Meta;

public interface ScopeHandler {
    <T> T get(Meta<T> meta, Context context) throws ScopeHandlerException;
}
