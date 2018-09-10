package io.github.sbnarra.injection.scope;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.meta.Meta;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScopeHandler implements ScopeHandler {

    private final ThreadLocal<Map<Meta<?>, Object>> threadLocals = new ThreadLocal<>();

    @Override
    public void destoryScope() throws ScopeHandlerException {
        threadLocals.remove();
    }

    @Override
    public <T> T get(Meta<T> meta, Injector injector) throws ScopeHandlerException {
        Map<Meta<?>, Object> map = getMap();

        Object obj = map.get(meta);
        if (obj == null) {
            synchronized (map) {
                obj = map.get(meta);
                if (obj == null) {
                    return storeConstructed(map, meta, injector);
                }
            }
        }
        return meta.getClazz().getBuildClass().cast(obj);
    }

    private <T> T storeConstructed(Map<Meta<?>, Object> map, Meta<T> meta, Injector injector) throws ScopeHandlerException {
        try {
            T constructed = injector.context().construct(meta, injector);
            map.put(meta, constructed);
            return constructed;
        } catch (ContextException e) {
            throw new ScopeHandlerException("error creating instance: " + meta, e);
        }
    }

    private Map<Meta<?>, Object> getMap() {
        Map<Meta<?>, Object> theMap = threadLocals.get();
        if (theMap == null) {
            threadLocals.set(theMap = new HashMap<>());
        }
        return theMap;
    }
}
