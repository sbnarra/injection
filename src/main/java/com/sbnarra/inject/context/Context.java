package com.sbnarra.inject.context;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.Registry;
import com.sbnarra.inject.TypeBinding;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.graph.GraphException;
import com.sbnarra.inject.graph.Node;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RequiredArgsConstructor
public class Context {
    private final Registry registry;
    private final Graph graph;

    private final ScopedContext scopedContext;

    public <T> T get(Type<T> type, Qualifier qualifier) throws ContextException {
        Node<T> found = lookup(type, qualifier);
        return internalGet(found.getMeta());
    }

    private <T> T internalGet(Meta<T> meta) throws ContextException {
        if (meta.getScoped() != null) {
            return scopedContext.get(meta, this);
        }
        return construct(meta);
    }

    public <T> Node lookup(Class<T> tClass, Qualifier qualifier) throws ContextException {
        return lookup(new Type<T>(tClass) {}, qualifier);
    }

    public <T> Node lookup(Type<T> theType, Qualifier qualifier) throws ContextException {
        Node node = graph.find(theType,  qualifier);
        if (node == null) {
            try {
                if (qualifier == null) {
                    node = graph.addNode(new TypeBinding<>(theType).with(theType).getBinding(), this);
                } else {
                    TypeBinding<?> typeBinding = registry.find(theType, qualifier);
                    if (typeBinding == null) {
                        throw new ContextException("no binding found for: type=" + theType + ",qualifier=" + qualifier);
                    }
                    Debug.log("registry: " + typeBinding);
                    node = graph.addNode(typeBinding, this);
                }

            } catch (GraphException e) {
                throw new ContextException("error adding node to graph during lookup: " + theType + ": qualifier: " + qualifier, e);
            }
        }
        return node;
    }

    public  <T> T construct(@NonNull Meta<T> meta) throws ContextException {
        if (meta.getInstance() != null) {
            return meta.getInstance();
        }

        Meta.Constructor<T> constructorMeta = meta.getConstructor();
        java.lang.reflect.Constructor<T> constructor = constructorMeta.getConstructor();

        T newInstance;
        try {
            newInstance = constructor.newInstance(getParameters(constructorMeta.getFields()));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContextException("error building new instance", e);
        }

        injectMembers(newInstance, meta);
        return newInstance;
    }

    private <T> void injectMembers(T t, Meta<T> meta) throws ContextException {
        for (Meta.Field field : meta.getField()) {
            try {
                field.getField().set(t, construct(field.getMeta()));
            } catch (IllegalAccessException e) {
                throw new ContextException("failed to inject field: " + field, e);
            }
        }

        for (Meta.Method method : meta.getMethod()) {
            try {
                method.getMethod().invoke(t, getParameters(method.getFields()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ContextException("failed to inject method: " + method, e);
            }
        }
    }

    private Object[] getParameters(List<Meta> argMetas) throws ContextException {
        Object[] args = new Object[argMetas.size()];
        for (int i = 0; i < argMetas.size(); i++) {
            args[i] = construct(argMetas.get(i));
        }
        return args;
    }

    public Registry getRegistry() {
        return registry;
    }
}
