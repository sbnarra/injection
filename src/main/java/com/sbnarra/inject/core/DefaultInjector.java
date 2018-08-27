package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.Injector;
import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.meta.Qualifier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultInjector implements Injector {

    private final Context context;

    @Override
    public <T> T get(Type<T> type, Qualifier qualifier) throws InjectException {
        try {
            return context.get(type, qualifier);
        } catch (ContextException e) {
            throw new InjectException("failed to get: " + type, e);
        }
    }
}
