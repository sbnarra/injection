package com.sbnarra.inject;

import com.sbnarra.inject.registry.Type;

public interface Injector {

    <T> T get(Class<T> tClass) throws InjectException;

    <T> T get(Type<T> tClass) throws InjectException;
}
