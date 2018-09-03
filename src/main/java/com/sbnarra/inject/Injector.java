package com.sbnarra.inject;

import com.sbnarra.inject.core.NamedAnnotation;
import com.sbnarra.inject.core.Type;

import java.lang.annotation.Annotation;

public interface Injector {

    default <T> T get(Class<T> tClass) throws InjectException {
        return get(tClass, (Annotation) null);
    }

    default <T> T get(Type<T> tClass) throws InjectException {
        return get(tClass, (Annotation) null);
    }

    default <T> T get(Class<T> tClass, String named) throws InjectException {
        return get(tClass, new NamedAnnotation(named));
    }

    default <T> T get(Type<T> tClass, String named) throws InjectException {
        return get(tClass, new NamedAnnotation(named));
    }

    default <T> T get(Class<T> tClass, Annotation qualifier) throws InjectException {
        return get(new Type<T>(tClass) {}, qualifier);
    }

    <T> T get(Type<T> tClass, Annotation qualifier) throws InjectException;
}
