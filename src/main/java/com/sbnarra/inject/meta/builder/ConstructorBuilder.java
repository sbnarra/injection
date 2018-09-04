package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.meta.Meta;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.util.List;

@RequiredArgsConstructor
class ConstructorBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    <T> Meta.Constructor<T> build(Meta.Class<T> classMeta, Context context) throws BuilderException {
        Constructor<? extends T> constructor = find(classMeta);
        List<Meta.Parameter> fieldMeta = parametersMetaBuilder.getParameters(constructor, context);

        constructor.setAccessible(true);
        return Meta.Constructor.<T>builder()
                .constructor(constructor)
                .parameters(fieldMeta)
                .build();
    }

    private <T> Constructor<? extends T> find(Meta.Class<T> classMeta) throws BuilderException {
        Class<?> bindClass = classMeta.getBindClass();
        Class<? extends T> buildClass = classMeta.getBuildClass();

        if (bindClass != buildClass) {
            return typedConstructorLookup(bindClass, buildClass);
        } else {
            List<Constructor<?>> constructors = Annotations.findInject(buildClass.getDeclaredConstructors());
            if (constructors.size() == 0) {
                return noArgConstructor(buildClass);
            } else if (constructors.size() > 1) {
                throw new BuilderException("multiple inject constructors");
            }
            return getTypeSafeConstructor(buildClass, constructors.get(0));
        }
    }

    private <T> Constructor<? extends T> typedConstructorLookup(@NonNull Class<?> bindClass, @NonNull Class<? extends T> buildClass) throws BuilderException {
        Constructor<?>[] constructors = bindClass.getDeclaredConstructors();
        List<Integer> injectIndexes = Annotations.findInjectIndexes(constructors);
        if (injectIndexes.size() == 0) {
            return noArgConstructor(buildClass);
        } else if (injectIndexes.size() > 1) {
            throw new BuilderException("multiple inject constructors on " + bindClass);
        }

        Constructor<?> constructor = buildClass.getDeclaredConstructors()[constructors.length - injectIndexes.get(0)];
        return getTypeSafeConstructor(buildClass, constructor);
    }

    private <T> Constructor<? extends T> getTypeSafeConstructor(Class<? extends T> theClass, Constructor<?> constructor) throws BuilderException {
        try {
            return theClass.getDeclaredConstructor(constructor.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new BuilderException("failed to create type safe constructor", e);
        }
    }

    private <T> Constructor<? extends T> noArgConstructor(Class<? extends T> theClass) throws BuilderException {
        try {
            return theClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BuilderException(theClass + ": missing no-arg or inject annotated constructors");
        }
    }
}
