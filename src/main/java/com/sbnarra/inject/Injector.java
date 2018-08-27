package com.sbnarra.inject;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Qualifier;

public interface Injector {

    default <T> T get(Class<T> tClass) throws InjectException {
        return get(tClass, (Qualifier) null);
    }

    default <T> T get(Type<T> tClass) throws InjectException {
        return get(tClass, (Qualifier) null);
    }

    default <T> T get(Class<T> tClass, String named) throws InjectException {
        return get(tClass, new Qualifier.Named(named));
    }

    default <T> T get(Type<T> tClass, String named) throws InjectException {
        return get(tClass, new Qualifier.Named(named));
    }

    default <T> T get(Class<T> tClass, Qualifier qualifier) throws InjectException {
        return get(new Type<T>(tClass) {}, qualifier);
    }

    <T> T get(Type<T> tClass, Qualifier qualifier) throws InjectException;
}
