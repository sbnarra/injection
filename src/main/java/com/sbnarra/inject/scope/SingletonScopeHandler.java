package com.sbnarra.inject.scope;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.meta.Meta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonScopeHandler implements ScopeHandler {
    private final Map<Meta, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public void destoryScope() throws ScopeHandlerException {
        singletons.clear();
    }

    @Override
    public <T> T get(Meta<T> meta, Context context) throws ScopeHandlerException {
        Object singleton = singletons.get(meta);
        if (singleton != null) {
            return meta.getClazz().getBuildClass().cast(singleton);
        }

        T t;
        try {
            t = context.construct(meta);
        } catch (ContextException e) {
            throw new ScopeHandlerException("error creating instance: " + meta, e);
        }

        singletons.put(meta, t);
        return t;
    }
}
