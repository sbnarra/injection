package com.sbnarra.inject.core;

import lombok.ToString;

import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@ToString
public abstract class Type<T> {

    private Parameterized parameterized;
    private Class<T> theClass;

    private Type(Parameterized parameterized, Class<T> theClass) {
        this.parameterized = parameterized;
        this.theClass = theClass;
    }

    public Type(java.lang.reflect.Type type) {
        if (Class.class.isInstance(type)) {
            this.theClass = Class.class.cast(type);
        } else if (ParameterizedType.class.isInstance(type)) {
            this.parameterized = initType(type);
        }
    }

    public Type() {
        java.lang.reflect.Type thisType = ParameterizedType.class.cast(getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (Class.class.isInstance(thisType)) {
            this.theClass = Class.class.cast(thisType);
        } else if (ParameterizedType.class.isInstance(thisType)) {
            this.parameterized = initType(thisType);
        }
    }

    private static Parameterized initType(java.lang.reflect.Type type) {
        if (Class.class.isInstance(type)) {
            return handleClassType(Class.class.cast(type));
        } else if (ParameterizedType.class.isInstance(type)) {
            return handleParameterizedType(ParameterizedType.class.cast(type));
        } else {
            throw new RuntimeException("unknown type: " + type.getClass());
        }
    }

    private static Parameterized handleClassType(Class<?> theClass) {
        java.lang.reflect.Type genericSuperclass = theClass.getGenericSuperclass();
        if (genericSuperclass == null) {
            throw new RuntimeException(theClass + " has no generic supertype");
        } else if (ParameterizedType.class.isInstance(genericSuperclass)) {
            return handleParameterizedType(ParameterizedType.class.cast(genericSuperclass));
        } else if (Class.class.isInstance(genericSuperclass)) {
            throw new RuntimeException("unable to init class generic superclass: " + genericSuperclass);
        } else {
            throw new RuntimeException("unknown type: " + genericSuperclass.getClass());
        }
    }

    private static Parameterized handleParameterizedType(ParameterizedType parameterizedType) {
        boolean isProvider = Provider.class.isAssignableFrom(Class.class.cast(parameterizedType.getRawType()));
        Parameterized parameterized = new Parameterized(isProvider, parameterizedType);
        for (java.lang.reflect.Type generic : parameterizedType.getActualTypeArguments()) {
            List<Type<?>> generics = parameterized.getGenerics();
            if (ParameterizedType.class.isInstance(generic)) {
                generics.add(new Type<Object>(handleParameterizedType(ParameterizedType.class.cast(generic)), null) {});
            } else if (Class.class.isInstance(generic)) {
                generics.add(new Type<Object>(generic) {});
            } else {
                throw new RuntimeException("unknown type: " + generic.getClass());
            }
        }
        return parameterized;
    }

    public boolean isParameterized() {
        return parameterized != null;
    }

    public java.lang.Class<T> getTheClass() {
        return isParameterized() ? parameterized.getRawType() : theClass;
    }

    public boolean isProvider() {
        return isParameterized() ? parameterized.isProvider() : false;
    }

    public Parameterized<T> getParameterized() {
        return parameterized;
    }
}
