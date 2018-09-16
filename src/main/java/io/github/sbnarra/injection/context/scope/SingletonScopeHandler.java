package io.github.sbnarra.injection.context.scope;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.meta.Meta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonScopeHandler implements ScopeHandler {
    private final Map<Meta, Object> singletons = new ConcurrentHashMap<>();

    @Override
    public void destoryScope() {
        singletons.clear();
    }

    @Override
    public <T> T get(Meta<T> meta, Meta.Inject inject, Injector injector) throws ContextException {
        Object singleton = singletons.get(meta);
        if (singleton != null) {
            return meta.getClazz().getBuildClass().cast(singleton);
        }

        // constructor from context not the injector to avoid cyclic injection
        T t = injector.context().objectBuilder().construct(meta, injector);
        singletons.put(meta, t);
        return t;
    }
}
