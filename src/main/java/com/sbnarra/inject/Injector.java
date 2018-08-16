package com.sbnarra.inject;

import com.sbnarra.inject.registry.Type;

public interface Injector {

    default <T> T get(Class<T> tClass) throws InjectException {
        return get(tClass, null);
    }

    <T> T get(Class<T> tClass, String named) throws InjectException;

    default <T> T get(Type<T> tClass) throws InjectException {
        return get(tClass, null);
    }

    <T> T get(Type<T> tClass, String named) throws InjectException;
}
