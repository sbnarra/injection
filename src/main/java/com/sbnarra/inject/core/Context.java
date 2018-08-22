package com.sbnarra.inject.core;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.registry.Registry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RequiredArgsConstructor
public class Context {
    private final Registry registry;
    private final Graph graph;

    private final ScopedContext scopedContext;

    public <T> T get(Class<T> tClass, Qualifier qualifier) throws InjectException {
        Graph.Node<T> found = lookup(tClass, qualifier);
        return internalGet(found.getMeta());
    }

    public <T> T get(Type<T> type, Qualifier qualifier) throws InjectException {
        Graph.Node<T> found = lookup(type, qualifier);
        return internalGet(found.getMeta());
    }

    private <T> T internalGet(Meta<T> meta) throws InjectException {
        if (meta.getScoped() != null) {
            return scopedContext.get(meta, this);
        }
        return constructInjected(meta);
    }

    public <T> Graph.Node lookup(Class<T> tClass, Qualifier qualifier) throws InjectException {
        return lookup(new Type<T>(tClass) {}, qualifier);
    }

    public <T> Graph.Node lookup(Type<T> theType, Qualifier qualifier) throws InjectException {
        Graph.Node node = graph.find(theType,  qualifier);
        if (node == null) {// TODO and qualifier != null
            Debug.log("node not found, adding self-binding");
            throw new InjectException(theType + ": node not found, adding self-binding");
            //node = graph.addNode(new TypeBinding<>(theType).qualified(qualifier).with(theType).getBinding(), this);
        }
        Debug.log(node);
        return node;
    }

    public <T> T constructInjected(Meta<T> meta) throws InjectException {
        T constructed = construct(meta);
        injectMembers(constructed, meta);
        return constructed;
    }

    private <T> T construct(@NonNull Meta meta) throws InjectException {
        Meta.Constructor constructorMeta = meta.getConstructor();
        java.lang.reflect.Constructor<T> constructor = constructorMeta.getConstructor();

        T newInstance;
        try {
            newInstance = constructor.newInstance(getParameters(constructorMeta.getFields()));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InjectException("error building new instance", e);
        }

        injectMembers(newInstance, meta);
        return newInstance;
    }

    private <T> void injectMembers(T t, Meta<T> meta) throws InjectException {
        for (Meta.Field field : meta.getField()) {
            try {
                field.getField().set(t, construct(field.getMeta()));
            } catch (IllegalAccessException e) {
                throw new InjectException("failed to inject field: " + field, e);
            }
        }

        for (Meta.Method method : meta.getMethod()) {
            try {
                method.getMethod().invoke(t, getParameters(method.getFields()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InjectException("failed to inject method: " + method, e);
            }
        }
    }

    private Object[] getParameters(List<Meta> argMetas) throws InjectException {
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
