package com.sbnarra.inject.resolver;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.InjectionAnnotations;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ConstructorMeta;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

@RequiredArgsConstructor
class ConstructorResolver {
    private final InjectionAnnotations injectionAnnotations;
    private final FieldResolver fieldResolver;

    <T> ConstructorMeta resolve(ClassMeta classMeta) throws InjectException {
        Constructor<T> constructor = find(classMeta);
        return ConstructorMeta.<T>builder()
                .constructor(constructor)
                .fields(fieldResolver.resolve(constructor.getParameterTypes()))
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
        throw new InjectException(buildClass + ": no inject annotation constructors, available inject annotations are: " + injectionAnnotations.injectAnnotations());
    }

    private <T> Constructor typedConstructorLookup(Class<T> bindClass, Class<?> buildClass) throws InjectException {
        Constructor<?>[] constructors = bindClass.getDeclaredConstructors();
        for (int i = constructors.length; i > -1; i--) {
            for (Class<Annotation> annotationClass : injectionAnnotations.injectAnnotations()) {
                Constructor<?> constructor = constructors[i];
                if (constructor.getAnnotation(annotationClass) != null) {
                    return buildClass.getDeclaredConstructors()[i];
                }
            }
        }
        throw new InjectException(bindClass + ": no inject annotation constructors, available inject annotations are: " + injectionAnnotations.injectAnnotations());
    }
}
