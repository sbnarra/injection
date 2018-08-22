package com.sbnarra.inject.core;

import com.sbnarra.inject.InjectException;
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

    public Type(T instance) {
        this(null, null);
    }

    public Type(java.lang.Class theClass) {
        this(null, theClass);
    }

    public Type(java.lang.reflect.Type type) {
        if (java.lang.reflect.ParameterizedType.class.isInstance(type)) {
            this.theClass = null;
            this.parameterized = new Parameterized(java.lang.reflect.ParameterizedType.class.cast(type));
            gatherGenerics(this.parameterized.getType(), this.parameterized.getGenerics());
        } else if (java.lang.Class.class.isInstance(type)) {
            this.parameterized = null;
            this.theClass = java.lang.Class.class.cast(type);
        } else {
            throw new RuntimeException("unknown type: " + type.getClass());
        }
    }

    public Type() throws InjectException {
        java.lang.reflect.Type genericSuperclass = this.getClass().getGenericSuperclass();
        java.lang.reflect.Type typeParameter = java.lang.reflect.ParameterizedType.class.cast(genericSuperclass).getActualTypeArguments()[0];
        if (!java.lang.reflect.ParameterizedType.class.isInstance(typeParameter)) {
            throw new InjectException(getClass() + ": is not Parameterized");
        }
        java.lang.reflect.ParameterizedType parameterizedTypeParameter = java.lang.reflect.ParameterizedType.class.cast(typeParameter);

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
