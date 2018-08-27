package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.meta.Meta;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class ConstructorBuilder {
    private final Annotations annotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    <T> Meta.Constructor build(Meta.Class classMeta, Context context) throws BuilderException {
        java.lang.reflect.Constructor<T> constructor = find(classMeta);
        List<Meta.Parameter> fieldMeta = parametersMetaBuilder.getParameters(constructor, context);

        constructor.setAccessible(true);
        return Meta.Constructor.<T>builder()
                .constructor(constructor)
                .parameters(fieldMeta)
                .build();
    }


    private <T> java.lang.reflect.Constructor<T> find(Meta.Class classMeta) throws BuilderException {
        if (classMeta.getBindClass() != classMeta.getBuildClass()) {
            return typedConstructorLookup(classMeta.getBindClass(), classMeta.getBuildClass());
        } else {
            return constructorLookup(classMeta.getBuildClass());
        }
    }

    private <T> java.lang.reflect.Constructor<T> constructorLookup(Class buildClass) throws BuilderException {
        for(java.lang.reflect.Constructor constructor : buildClass.getDeclaredConstructors()) {
            for (Class annotationClass : annotations.getInject()) {
                if (constructor.getAnnotation(annotationClass) != null) {
                    return constructor;
                }
            }
        }
        return noArgConstructor(buildClass);
    }

    private <T> java.lang.reflect.Constructor<T> typedConstructorLookup(@NonNull Class bindClass, @NonNull Class buildClass) throws BuilderException {
        java.lang.reflect.Constructor[] constructors = bindClass.getDeclaredConstructors();
        for (int i = constructors.length-1; i > -1; i--) {
            for (Class annotationClass : annotations.getInject()) {
                java.lang.reflect.Constructor constructor = constructors[i];
                if (constructor.getAnnotation(annotationClass) != null) {
                    return buildClass.getDeclaredConstructors()[i];
                }
            }
        }
       return noArgConstructor(buildClass);
    }

    private <T> java.lang.reflect.Constructor<T> noArgConstructor(Class theClass) throws BuilderException {
        try {
            return theClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BuilderException(theClass + ": missing no-arg or inject annotated constructors, available inject annotations are: " + annotations.getInject());
        }
    }
}
