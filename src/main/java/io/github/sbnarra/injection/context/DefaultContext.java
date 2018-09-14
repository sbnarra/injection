package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Helper;
import io.github.sbnarra.injection.Injector;
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

import javax.inject.Provider;
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
    public <T> T get(Meta<T> meta, Injector injector) throws ContextException {
        if (meta.getClazz().getInject().getScoped() != null) {
            return scopedContext.get(meta, injector);
        }
        return construct(meta, injector);
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
    public <T> Node<?> lookup(Type<T> theType, Annotation qualifier, Annotation scope) throws ContextException {
        try {
            Node<?> node = graph.find(theType,  qualifier);
            if (node != null) {
                return node;
            }
        } catch (GraphException e) {
            throw new ContextException("error looking up type: " + theType + ": qualifier: " + qualifier);
        }

        try {
            if (qualifier == null) {
                try {
                    Helper.checkBuildability(theType);
                } catch (Helper.HelperException e) {
                    throw new ContextException(e.getMessage());
                }
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

    private <T> TypeBinding<T> selfBinding(Type<T> theType) throws ContextException {
        try {
            return new TypeBinding<>(theType, registry().getTypeBindings())
                    .with(theType)
                    .getBinding();
        } catch (RegistryException e) {
            throw new ContextException("error creating self-binding: " + theType, e);
        }
    }

    @Override
    public  <T> T construct(@NonNull Meta<T> meta, Injector injector) throws ContextException {
        if (meta.getInstance() != null) {
            return meta.getInstance();
        }

        Meta.Constructor<T> constructorMeta = meta.getConstructor();
        java.lang.reflect.Constructor<? extends T> constructor = constructorMeta.getConstructor();

        Object[] args = getParameters(constructorMeta.getParameters(), injector);

        T newInstance;
        try {
            newInstance = constructor.newInstance(args);
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContextException("error building new instance using: constructor: "
                    + constructor + ",args=" + Arrays.toString(args), e);
        }

        injectMembers(newInstance, meta, injector);
        return newInstance;
    }

    int c = 0;

    private <T> void injectMembers(T t, Meta<T> meta, Injector injector) throws ContextException {
        List<Meta.Members> members = meta.getMembers();
        int cc = c++;
        for (int i = 0; i < members.size(); i++) {
            injectMembers(t, members.get(i), injector);
        }
    }

    private <T> void injectMembers(T t, Meta.Members metaMembers, Injector injector) throws ContextException {
        for (Meta.Field fieldMeta : metaMembers.getFields()) {
            Object fieldValue;
            Meta.Parameter parameterMeta = fieldMeta.getParameter();
            if (Meta.InstanceParameter.class.isInstance(parameterMeta)) {
                Meta.InstanceParameter instanceParameter = Meta.InstanceParameter.class.cast(parameterMeta);
                fieldValue = get(instanceParameter.getMeta(), injector);
            } else {
                fieldValue = getDefaultProvider(Meta.ProviderParameter.class.cast(parameterMeta), injector);
            }

            try {
                fieldMeta.getField().set(t, fieldValue);
            } catch (IllegalAccessException e) {
                throw new ContextException("failed to inject field: " + fieldMeta, e);
            }
        }

        for (Meta.Method method : metaMembers.getMethods()) {
            try {
                method.getMethod().invoke(t, getParameters(method.getParameters(), injector));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ContextException("failed to inject method: " + method, e);
            }
        }
    }

    private Object[] getParameters(List<Meta.Parameter> argMetas, Injector injector) throws ContextException {
        Object[] args = new Object[argMetas.size()];
        for (int i = 0; i < argMetas.size(); i++) {
            Meta.Parameter paramMeta = argMetas.get(i);
            if (Meta.ProviderParameter.class.isInstance(paramMeta)) {
                Meta.ProviderParameter providerParameter = Meta.ProviderParameter.class.cast(paramMeta);
                args[i] = getDefaultProvider(providerParameter, injector);
            } else {
                Meta.InstanceParameter instanceParameter = Meta.InstanceParameter.class.cast(paramMeta);
                args[i] = get(instanceParameter.getMeta(), injector);
            }
        }
        return args;
    }

    private <T> Provider<T> getDefaultProvider(Meta.ProviderParameter<T> providerParameter, Injector injector) {
        return new DefaultProvider<>(providerParameter.getType(), injector, providerParameter.getInject().getQualifier(), providerParameter.getInject().getScoped());
    }
}
