package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Helper;
import io.github.sbnarra.injection.core.Debug;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.graph.Graph;
import io.github.sbnarra.injection.graph.GraphException;
import io.github.sbnarra.injection.graph.Node;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.RegistryException;
import io.github.sbnarra.injection.registry.TypeBinding;
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
    public ScopedContext scopedContext() {
        return scopedContext;
    }

    @Override
    public Registry registry() {
        return registry;
    }

    @Override
    public <T> Node<?> lookup(Type<T> theType, Annotation qualifier) throws ContextException {
        if (theType.isProvider()) {
            throw new ContextException("what to do"); // TODO  - remove
        }
        Debug.log(theType);

        try {
            Node<?> node = graph.find(theType,  qualifier);
            if (node != null) {
                return node;
            }
        } catch (GraphException e) {
            throw new ContextException("error looking up type: " + theType + ": qualifier: " + qualifier); // $COVERAGE-IGNORE$
        }

        try {
            if (qualifier == null) {
                try {
                    Helper.checkBuildability(theType);
                } catch (Helper.HelperException e) {
                    throw new ContextException(e.getMessage()); // $COVERAGE-IGNORE$
                }
                return graph.addNode(selfBinding(theType), this);
            }

            TypeBinding<?> typeBinding = registry.find(theType, qualifier);
            if (typeBinding == null) {
                throw new ContextException("no binding found for: type=" + theType + ",qualifier=" + qualifier); // $COVERAGE-IGNORE$
            }
            return graph.addNode(typeBinding, this);
        } catch (GraphException e) {
            throw new ContextException("error adding node to graph during lookup: " + theType + ": qualifier: " + qualifier, e); // $COVERAGE-IGNORE$
        }
    }

    private <T> TypeBinding<T> selfBinding(Type<T> theType) throws ContextException {
        try {
            return new TypeBinding<>(theType, registry().getTypeBindings())
                    .with(theType)
                    .getBinding();
        } catch (RegistryException e) {
            throw new ContextException("error creating self-binding: " + theType, e); // $COVERAGE-IGNORE$
        }
    }

    @Override
    public  <T> T construct(@NonNull Meta<T> meta) throws ContextException {
        if (meta.getInstance() != null) {
            return meta.getInstance();
        }

        Meta.Constructor<T> constructorMeta = meta.getConstructor();
        java.lang.reflect.Constructor<? extends T> constructor = constructorMeta.getConstructor();

        Object[] args = getParameters(constructorMeta.getParameters());

        T newInstance;
        try {
            newInstance = constructor.newInstance(args);
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContextException("error building new instance using: constructor: " // $COVERAGE-IGNORE$
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
                throw new ContextException("failed to inject field: " + field, e); // $COVERAGE-IGNORE$
            }
        }

        for (Meta.Method method : meta.getMethod()) {
            try {
                method.getMethod().invoke(t, getParameters(method.getParameters()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ContextException("failed to inject method: " + method, e); // $COVERAGE-IGNORE$
            }
        }
    }

    private Object[] getParameters(List<Meta.Parameter> argMetas) throws ContextException {
        Object[] args = new Object[argMetas.size()];
        for (int i = 0; i < argMetas.size(); i++) {
            Meta.Parameter paramMeta = argMetas.get(i);
            if (paramMeta.isProvider()) {
                args[i] = new DefaultProvider<>(paramMeta.getMeta(), this);
            } else {
                args[i] = construct(paramMeta.getMeta());
            }
        }
        return args;
    }

}
