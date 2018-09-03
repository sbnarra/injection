package com.sbnarra.inject.scope;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.meta.Meta;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScopeHandler implements ScopeHandler {

    private final ThreadLocal<Map<Meta<?>, Object>> threadLocals = new ThreadLocal<>();

    @Override
    public void destoryScope() throws ScopeHandlerException {
        threadLocals.remove();
    }

    @Override
    public <T> T get(Meta<T> meta, Context context) throws ScopeHandlerException {
        Map<Meta<?>, Object> map = getMap();

        Object obj = map.get(meta);
        if (obj == null) {
            synchronized (map) {
                obj = map.get(meta);
                if (obj == null) {
                    return storeConstructed(map, meta, context);
                }
            }
        }
        return meta.getClazz().getBuildClass().cast(obj);
    }

    private <T> T storeConstructed(Map<Meta<?>, Object> map, Meta<T> meta, Context context) throws ScopeHandlerException {
        try {
            T constructed = context.construct(meta);
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
