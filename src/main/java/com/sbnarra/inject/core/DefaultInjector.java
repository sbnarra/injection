package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.Injector;
import com.sbnarra.inject.meta.Qualifier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultInjector implements Injector {

    private final Context context;

    @Override
    public <T> T get(Class<T> tClass, Qualifier qualifier) throws InjectException {
        return context.get(tClass, qualifier);
    }

    @Override
    public <T> T get(Type<T> type, Qualifier qualifier) throws InjectException {
        return context.get(type, qualifier);
    }
}
