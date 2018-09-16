package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.graph.Node;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.Registry;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface Context {

    default <T> Node lookup(@NonNull Class<T> tClass, Annotation qualifier, Annotation scope, Set<Class<?>> staticsMembers) throws ContextException {
        return lookup(new Type<T>(tClass) {}, qualifier, scope, staticsMembers);
    }

    <T> Node lookup(Type<T> theType, Annotation qualifier, Annotation scope, Set<Class<?>> staticsMembers) throws ContextException;

    default  <T> T get(Type<T> type, Annotation qualifier, Annotation scope, Injector injector, Set<Class<?>> staticsMembers) throws ContextException {
        Node<T> found = lookup(type, qualifier, scope, staticsMembers);
        return get(found.getMeta(), injector);
    }

    <T> T get(Meta<T> meta, Injector injector) throws ContextException;

    <T> T construct(@NonNull Meta<T> meta, Injector injector) throws ContextException;

    ScopedContext scopedContext();

    Registry registry();
}
