package com.sbnarra.inject.context;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.graph.GraphException;
import com.sbnarra.inject.graph.Node;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.TypeBinding;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultContext implements Context {
    private final Registry registry;
    private final Graph graph;

    private final ScopedContext scopedContext;

    @Override
    public <T> T get(Meta<T> meta) throws ContextException {
        if (meta.getClazz().getInject().getScoped() != null) {
            return scopedContext.get(meta, this);
        }
        return construct(meta);
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public <T> Node<?> lookup(Type<T> theType, Annotation qualifier) throws ContextException {

        Node<?> node = graph.find(theType,  qualifier);
        if (node != null) {
            return node;
        }

        try {
            if (qualifier == null) {
                return graph.addNode(selfBinding(theType), this);
            }

            TypeBinding<?> typeBinding = registry.find(theType, qualifier);
            if (typeBinding == null) {
                throw new ContextException("no binding found for: type=" + theType + ",qualifier=" + qualifier);
            }
            return graph.addNode(typeBinding, this);
        } catch (GraphException e) {
            throw new ContextException("error adding node to graph during lookup: " + theType + ": qualifier: " + qualifier, e);
        }
    }

    private <T> TypeBinding<T> selfBinding(Type<T> theType) {
        return new TypeBinding<T>(theType, getRegistry().getTypeBindings())
                .with(theType)
                .getBinding();
    }

    @Override
    public  <T> T construct(@NonNull Meta<T> meta) throws ContextException {
        if (meta.getInstance() != null) {
            return meta.getInstance();
        }

        Meta.Constructor<T> constructorMeta = meta.getConstructor();
        java.lang.reflect.Constructor<T> constructor = constructorMeta.getConstructor();

        Object[] args = getParameters(constructorMeta.getParameters());

        T newInstance;
        try {
            newInstance = constructor.newInstance(args);
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContextException("error building new instance using: constructor: "
                    + constructor + ",args=" + Arrays.toString(args), e);
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
                method.getMethod().invoke(t, getParameters(method.getParameters()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ContextException("failed to inject method: " + method, e);
            }
        }
    }

    private Object[] getParameters(List<Meta.Parameter> argMetas) throws ContextException {
        Object[] args = new Object[argMetas.size()];
        for (int i = 0; i < argMetas.size(); i++) {
            Meta.Parameter paramMeta = argMetas.get(i);
            if (paramMeta.isUseProvider()) {
                args[i] = new DefaultProvider<>(paramMeta.getMeta(), this);
            } else {
                args[i] = construct(paramMeta.getMeta());
            }
        }
        return args;
    }

}
