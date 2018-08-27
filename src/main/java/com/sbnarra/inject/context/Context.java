package com.sbnarra.inject.context;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.graph.Node;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.registry.Registry;
import lombok.NonNull;

public interface Context {

    default <T> Node lookup(@NonNull Class<T> tClass, Qualifier qualifier) throws ContextException {
        return lookup(new Type<T>(tClass) {}, qualifier);
    }

    <T> Node lookup(Type<T> theType, Qualifier qualifier) throws ContextException;

    default  <T> T get(Type<T> type, Qualifier qualifier) throws ContextException {
        Node<T> found = lookup(type, qualifier);
        return get(found.getMeta());
    }

    <T> T get(Meta<T> meta) throws ContextException;

    <T> T construct(@NonNull Meta<T> meta) throws ContextException;

    Registry getRegistry();
}
