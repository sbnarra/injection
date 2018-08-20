package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.Meta;

import java.util.HashMap;
import java.util.Map;

public class SingletonScopeHandler implements ScopeHandler {
    private final Map<Meta, Object> singletons = new HashMap<>();

    @Override
    public <T> T get(Meta<T> meta, Context context) throws InjectException {
        Object singleton = singletons.get(meta);
        if (singleton != null) {
            return meta.getClazz().getBuildClass().cast(singleton);
        }
        T t = context.constructInjected(meta);
        singletons.put(meta, t);
        return t;
    }
}
