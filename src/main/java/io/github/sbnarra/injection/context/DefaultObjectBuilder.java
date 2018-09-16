package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.meta.Meta;
import lombok.NonNull;

import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DefaultObjectBuilder implements ObjectBuilder {

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

        injectMembers(newInstance, meta.getMembers(), injector);
        return newInstance;
    }

    private <T> void injectMembers(T t, List<Meta.Members> members, Injector injector) throws ContextException {
        try {
            reverse(members).forEach(member -> {
                try {
                    injectFields(t, member.getFields(), injector);
                    injectMethods(t, member.getMethods(), injector);
                } catch (ContextException e) {
                    throw e.unchecked();
                }
            });
        } catch (ContextException.Unchecked e) {
            throw e.checked(ContextException.class);
        }
    }

    private <T> Stream<T> reverse(List<T> list) {
        return IntStream.range(0, list.size()).mapToObj(i -> list.get(list.size() - i - 1));
    }

    private <T> void injectFields(T t, List<Meta.Field> fields, Injector injector) throws ContextException {
        for (Meta.Field fieldMeta : fields) {
            Object fieldValue;
            Meta.Parameter parameterMeta = fieldMeta.getParameter();
            if (Meta.InstanceParameter.class.isInstance(parameterMeta)) {
                Meta.InstanceParameter instanceParameter = Meta.InstanceParameter.class.cast(parameterMeta);
                fieldValue = injector.context().get(instanceParameter.getMeta(), instanceParameter.getInject(), injector);
            } else {
                fieldValue = getDefaultProvider(Meta.ProviderParameter.class.cast(parameterMeta), injector);
            }

            try {
                fieldMeta.getField().set(t, fieldValue);
            } catch (IllegalAccessException e) {
                throw new ContextException("failed to inject field: " + fieldMeta, e);
            }
        }
    }

    private <T> void injectMethods(T t, List<Meta.Method> methods, Injector injector) throws ContextException {
        for (Meta.Method method : methods) {
            try {
                Object[] args = getParameters(method.getParameters(), injector);
                method.getMethod().invoke(t, args);
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
                Meta.ProviderParameter<?> providerParameter = Meta.ProviderParameter.class.cast(paramMeta);
                args[i] = getDefaultProvider(providerParameter, injector);
            } else {
                Meta.InstanceParameter instanceParameter = Meta.InstanceParameter.class.cast(paramMeta);
                args[i] = injector.context().get(instanceParameter.getMeta(), instanceParameter.getInject(), injector);
            }
        }
        return args;
    }

    private <T> Provider<T> getDefaultProvider(Meta.ProviderParameter<T> providerParameter, Injector injector) {
        return new DefaultProvider<>(providerParameter.getType(), injector, providerParameter.getInject().getQualifier(), providerParameter.getInject().getScoped());
    }
}
