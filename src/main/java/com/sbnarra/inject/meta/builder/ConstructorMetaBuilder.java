package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.InjectionAnnotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ConstructorMeta;
import com.sbnarra.inject.registry.Registry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

@RequiredArgsConstructor
class ConstructorMetaBuilder {
    private final InjectionAnnotations injectionAnnotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    <T> ConstructorMeta build(ClassMeta classMeta, Graph graph, Registry registry) throws InjectException {
        Constructor<T> constructor = find(classMeta);
        return ConstructorMeta.<T>builder()
                .constructor(constructor)
                .fields(parametersMetaBuilder.getParameters(constructor, graph, registry))
                .build();
    }


    private <T> Constructor<T> find(ClassMeta classMeta) throws InjectException {
        if (classMeta.getBindClass() != classMeta.getBuildClass()) {
            return typedConstructorLookup(classMeta.getBindClass(), classMeta.getBuildClass());
        } else {
            return constructorLookup(classMeta.getBuildClass());
        }
    }

    private <T> Constructor constructorLookup(Class<T> buildClass) throws InjectException {
        for(Constructor<?> constructor : buildClass.getDeclaredConstructors()) {
            for (Class<Annotation> annotationClass : injectionAnnotations.injectAnnotations()) {
                if (constructor.getAnnotation(annotationClass) != null) {
                    return constructor;
                }
            }
        }
        return noArgConstructor(buildClass);
    }

    private <T> Constructor typedConstructorLookup(@NonNull Class<T> bindClass, @NonNull Class<?> buildClass) throws InjectException {
        Constructor<?>[] constructors = bindClass.getDeclaredConstructors();
        for (int i = constructors.length-1; i > -1; i--) {
            for (Class<Annotation> annotationClass : injectionAnnotations.injectAnnotations()) {
                Constructor<?> constructor = constructors[i];
                if (constructor.getAnnotation(annotationClass) != null) {
                    return buildClass.getDeclaredConstructors()[i];
                }
            }
        }
       return noArgConstructor(buildClass);
    }

    private <T> Constructor noArgConstructor(Class<T> theClass) throws InjectException {
        try {
            return theClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new InjectException(theClass + ": no inject annotation constructors, available inject annotations are: " + injectionAnnotations.injectAnnotations());
        }
    }
}
