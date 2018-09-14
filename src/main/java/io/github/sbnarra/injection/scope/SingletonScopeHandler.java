package io.github.sbnarra.injection.scope;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.meta.Meta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonScopeHandler implements ScopeHandler {
    private final Map<Meta, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public void destoryScope() throws ScopeHandlerException {
        singletons.clear();
    }

    @Override
    public <T> T get(Meta<T> meta, Meta.Inject inject, Injector injector) throws ScopeHandlerException {
        Object singleton = singletons.get(meta);
        if (singleton != null) {
            return meta.getClazz().getBuildClass().cast(singleton);
        }

        T t;
        try {
            // constructor from context not the injector to avoid cyclic injection
            t = injector.context().construct(meta, injector);
        } catch (ContextException e) {
            throw new ScopeHandlerException("error creating instance: " + meta, e);
        }

        singletons.put(meta, t);
        return t;
    }
}
