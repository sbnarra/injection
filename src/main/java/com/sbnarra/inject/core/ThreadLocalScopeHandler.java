package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.Meta;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalScopeHandler implements ScopeHandler {
    private final Map<Meta, ThreadLocal<Object>> threadLocals = new HashMap<>();
    @Override
    public <T> T get(Meta<T> meta, Context context) throws InjectException {
        ThreadLocal<Object> threadLocal = threadLocals.get(meta);
        if (threadLocal == null) {
            synchronized (threadLocals) {
                threadLocal = threadLocals.get(meta);
                if (threadLocal == null) {
                    threadLocals.put(meta, threadLocal = new ThreadLocal<>());
                    threadLocal.set(context.constructInjected(meta));
                }
            }
        }

        return meta.getClazz().getBuildClass().cast(threadLocal.get());
    }
}
