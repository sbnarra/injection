package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.graph.Node;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.Registry;
import lombok.NonNull;

import java.lang.annotation.Annotation;

public interface Context {


    default <T> Node lookup(@NonNull Class<T> tClass, Annotation qualifier) throws ContextException {
        return lookup(new Type<T>(tClass) {}, qualifier);
    }

    <T> Node lookup(Type<T> theType, Annotation qualifier) throws ContextException;

    default  <T> T get(Type<T> type, Annotation qualifier) throws ContextException {
        Node<T> found = lookup(type, qualifier);
        return get(found.getMeta());
    }

    <T> T get(Meta<T> meta) throws ContextException;

    <T> T construct(@NonNull Meta<T> meta) throws ContextException;

    ScopedContext scopedContext();

    Registry registry();
}
