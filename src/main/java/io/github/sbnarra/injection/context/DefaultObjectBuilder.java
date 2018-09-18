package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.misc.UncheckedException;
import lombok.NonNull;

import javax.inject.Provider;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
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
        checkedParallelForEach(fields, injectField(t, injector));
    }

    private <T> Consumer<Meta.Field> injectField(T t, Injector injector) {
        return fieldMeta -> {
            Object fieldValue;
            Meta.Parameter parameterMeta = fieldMeta.getParameter();
            if (parameterMeta instanceof Meta.InstanceParameter) {
                Meta.InstanceParameter instanceParameter = (Meta.InstanceParameter) parameterMeta;
                try {
                    fieldValue = injector.context().get(instanceParameter.getMeta(), instanceParameter.getInject(), injector);
                } catch (ContextException e) {
                    throw e.unchecked();
                }
            } else {
                fieldValue = getDefaultProvider((Meta.ProviderParameter) parameterMeta, injector);
            }

            try {
                fieldMeta.getField().set(t, fieldValue);
            } catch (IllegalAccessException e) {
                throw new ContextException("failed to inject field: " + fieldMeta, e).unchecked();
            }
        };
    }

    private <T> void injectMethods(T t, List<Meta.Method> methods, Injector injector) throws ContextException {
        checkedParallelForEach(methods, injectMethod(t, injector));
    }

    private <T> Consumer<Meta.Method> injectMethod(T t, Injector injector) {
        return method -> {
            try {
                try {
                    Object[] args = getParameters(method.getParameters(), injector);
                    method.getMethod().invoke(t, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ContextException("failed to inject method: " + method, e);
                }
            } catch (ContextException e) {
                throw e.unchecked();
            }
        };
    }

    private Object[] getParameters(List<Meta.Parameter> argMetas, Injector injector) throws ContextException {
        try {
            return IntStream.range(0, argMetas.size()).parallel().mapToObj(i -> {
                Meta.Parameter paramMeta = argMetas.get(i);
                if (paramMeta instanceof Meta.ProviderParameter) {
                    Meta.ProviderParameter<?> providerParameter = (Meta.ProviderParameter) paramMeta;
                    return getDefaultProvider(providerParameter, injector);
                } else {
                    Meta.InstanceParameter instanceParameter = (Meta.InstanceParameter) paramMeta;
                    try {
                        return injector.context().get(instanceParameter.getMeta(), instanceParameter.getInject(), injector);
                    } catch (ContextException e) {
                        throw e.unchecked();
                    }
                }
            }).toArray();
        } catch (ContextException.Unchecked e) {
            throw e.checked(ContextException.class);
        }
    }

    private <T> Provider<T> getDefaultProvider(Meta.ProviderParameter<T> providerParameter, Injector injector) {
        return new DefaultProvider<>(providerParameter.getType(), injector, providerParameter.getInject().getQualifier(), providerParameter.getInject().getScoped());
    }

    private static <T> void checkedParallelForEach(List<T> list, Consumer<T> consumer) throws ContextException {
        UncheckedException.checkedParallelForEach(list, consumer, ContextException.class);
    }
}
