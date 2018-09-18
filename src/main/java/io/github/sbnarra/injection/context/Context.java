package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.type.Type;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface Context {

    default <T> Meta<T> lookup(@NonNull Class<T> tClass, Annotation qualifier, Annotation scope, Set<Class<?>> staticsMembers) throws ContextException {
        return lookup(new Type<T>(tClass) {}, qualifier, scope, staticsMembers);
    }

    <T> Meta<T> lookup(Type<T> theType, Annotation qualifier, Annotation scope, Set<Class<?>> staticsMembers) throws ContextException;

    default  <T> T get(Meta<T> meta, Injector injector) throws ContextException {
        return get(meta, meta.getClazz().getInject(), injector);
    }

    default  <T> T get(Type<T> type, Annotation qualifier, Annotation scope, Injector injector, Set<Class<?>> staticsMembers) throws ContextException {
        return get(lookup(type, qualifier, scope, staticsMembers), injector);
    }

    <T> T get(Meta<T> meta, Meta.Inject inject, Injector injector) throws ContextException;

    ObjectBuilder objectBuilder();

    ScopedContext scopedContext();

    Registry registry();
}
