package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.UncheckedInjectException;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public abstract class Type<T> {

    @Value
    public class Parameterized {
        private final java.lang.reflect.ParameterizedType type;
        private final List<Type<?>> generics = new ArrayList<>();

        public java.lang.Class<?> getRawType() {
            return (java.lang.Class<?>) type.getRawType();
        }
    }

    private final Parameterized parameterized;
    private final java.lang.Class<?> theClass;

    private Type(Parameterized parameterized, java.lang.Class theClass) {
        this.theClass = theClass;
        this.parameterized = parameterized;
    }

    public Type(java.lang.Class theClass) {
        this(null, theClass);
    }

    public Type(java.lang.reflect.Type type) {
        if (type instanceof java.lang.reflect.ParameterizedType) {
            this.theClass = null;
            this.parameterized = new Parameterized((java.lang.reflect.ParameterizedType) type);
            gatherGenerics(this.parameterized.getType(), this.parameterized.getGenerics());
        } else if (type instanceof Class) {
            this.parameterized = null;
            this.theClass = (Class) type;
        } else {
            throw new RuntimeException("unknown type: " + type.getClass());
        }
    }

    public Type() {
        java.lang.reflect.Type genericSuperclass = this.getClass().getGenericSuperclass();
        java.lang.reflect.Type typeParameter = ((java.lang.reflect.ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        if (!(typeParameter instanceof java.lang.reflect.ParameterizedType)) {
            throw new UncheckedInjectException(new InjectException(getClass() + ": is not Parameterized"));
        }
        java.lang.reflect.ParameterizedType parameterizedTypeParameter = (java.lang.reflect.ParameterizedType) typeParameter;

        this.parameterized = new Parameterized(parameterizedTypeParameter);
        gatherGenerics(this.parameterized.getType(), this.parameterized.getGenerics());
        this.theClass = null;
    }

    private static void gatherGenerics(java.lang.reflect.ParameterizedType parameterizedType, List<Type<?>> generics) {
        for (java.lang.reflect.Type type : parameterizedType.getActualTypeArguments()) {
            generics.add(new Type<Object>(type) {});
        }
    }

    public java.lang.Class<?> getTheClass() {
        if (getParameterized() != null) {
            return getParameterized().getRawType();
        }
        return theClass;
    }
}
