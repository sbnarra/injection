package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.annotation.Annotations;
import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.meta.Meta;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
class ConstructorBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;
    private final Annotations annotations;

    <T> Meta.Constructor<T> build(Meta.Class<T> classMeta, Context context, Set<Class<?>> staticsMembers) throws BuilderException {
        Constructor<? extends T> constructor = find(classMeta);
        List<Meta.Parameter> fieldMeta = parametersMetaBuilder.buildParameters(constructor, context, staticsMembers);

        constructor.setAccessible(true);
        return Meta.Constructor.<T>builder()
                .constructor(constructor)
                .parameters(fieldMeta)
                .build();
    }

    private <T> Constructor<? extends T> find(Meta.Class<T> classMeta) throws BuilderException {
        Class<?> contractClass = classMeta.getContractClass();
        Class<? extends T> buildClass = classMeta.getBuildClass();

        if (buildClass != contractClass) {
            return typedConstructorLookup(buildClass, contractClass);
        } else {
            List<Constructor<?>> constructors = annotations.findAnnotatedElementsWithInjectAnnotation(buildClass.getDeclaredConstructors());
            if (constructors.size() == 0) {
                return noArgConstructor(buildClass);
            } else if (constructors.size() > 1) {
                throw new BuilderException("multiple inject constructors");
            }
            return getTypeSafeConstructor(buildClass, constructors.get(0));
        }
    }

    private <T> Constructor<? extends T> typedConstructorLookup(@NonNull Class<? extends T> buildClass, @NonNull Class<?> contractClass) throws BuilderException {
        Constructor<?>[] constructors = contractClass.getDeclaredConstructors();
        List<Integer> injectIndexes = annotations.findIndexesOfAnnotatedElementsWithInjectAnnotation(constructors);
        if (injectIndexes.size() == 0) {
            return noArgConstructor(buildClass);
        } else if (injectIndexes.size() > 1) {
            throw new BuilderException("multiple inject constructors on " + contractClass);
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
