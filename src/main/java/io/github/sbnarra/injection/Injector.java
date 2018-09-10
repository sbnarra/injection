package io.github.sbnarra.injection;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Named;
import io.github.sbnarra.injection.core.SimpleAnnotation;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.scope.ScopeHandlerException;

import javax.inject.Singleton;
import java.lang.annotation.Annotation;

public interface Injector {

    <T> T get(Type<T> type, Annotation qualifier, Annotation scope) throws InjectException;

    default <T> T get(Type<T> type) throws InjectException {
        return get(type, (Annotation) null, (Annotation) null);
    }

    default <T> T get(Type<T> type, Class<?> qualifier) throws InjectException {
        return get(type, new SimpleAnnotation((Class<? extends Annotation>) qualifier), (Annotation) null);
    }

    default <T> T get(Type<T> type, String named) throws InjectException {
        return get(type, new Named(named), (Annotation) null);
    }

    default <T> T get(Class<T> tClass, Class<?> qualifier) throws InjectException {
        return get(new Type<T>(tClass) {}, new SimpleAnnotation((Class<? extends Annotation>) qualifier), (Annotation) null);
    }

    default <T> T get(Class<T> tClass, Annotation qualifier) throws InjectException {
        return get(new Type<T>(tClass) {}, qualifier, (Annotation) null);
    }

    default <T> T get(Class<T> tClass) throws InjectException {
        return get(new Type<T>(tClass) {}, (Annotation) null, (Annotation) null);
    }

    default <T> T get(Class<T> tClass, String named) throws InjectException {
        return get(new Type<T>(tClass) {}, new Named(named), (Annotation) null);
    }

    default void destroyScope(Class<?> scopeClass) throws ScopeHandlerException {
        context().scopedContext().destoryScope(scopeClass);
    }

    default void destroySingletonScope() throws ScopeHandlerException {
        destroyScope(Singleton.class);
    }

    default void destroyThreadLocalScope() throws ScopeHandlerException {
        destroyScope(ThreadLocal.class);
    }

    Context context();
}
