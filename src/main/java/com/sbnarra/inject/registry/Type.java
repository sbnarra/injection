package com.sbnarra.inject.registry;

import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@ToString
public abstract class Type<T> {

    @Value
    public class Parameterized {
        private final java.lang.reflect.ParameterizedType type;
        private final List<Type<?>> generics = new ArrayList<>();
    }

    @Value
    public class Class {
        private final java.lang.Class<?> theClass;
    }

    private final Parameterized parameterized;
    private final Class clazz;

    public Type(java.lang.Class theClass) {
        this.clazz = new Class(theClass);
        this.parameterized = null;
    }

    public Type(java.lang.reflect.Type type) {
        log("new supplied type: " + type);
        if (java.lang.reflect.ParameterizedType.class.isInstance(type)) {
            log("Parameterized supplied type");
            this.clazz = null;
            this.parameterized = new Parameterized(java.lang.reflect.ParameterizedType.class.cast(type));
            gatherGenerics(this.parameterized.getType(), this.parameterized.getGenerics());
        } else if (java.lang.Class.class.isInstance(type)) {
            log("Class supplied type");
            this.parameterized = null;
            this.clazz = new Class(java.lang.Class.class.cast(type));
        } else {
            throw new RuntimeException("unknown type: " + type.getClass());
        }
    }

    public Type() {
        log("new runtime type: " + getClass() + ": " + java.lang.reflect.ParameterizedType.class.cast(getClass().getGenericSuperclass()));
        this.clazz = null;

        // get this superclass
        java.lang.reflect.Type genericSuperclass = getClass().getGenericSuperclass();
        // get the single argument this Type accepts
        java.lang.reflect.Type typeParameter = java.lang.reflect.ParameterizedType.class.cast(genericSuperclass).getActualTypeArguments()[0];
        // cast it to the correct ParameterizedType
        java.lang.reflect.ParameterizedType parameterizedTypeParameter = java.lang.reflect.ParameterizedType.class.cast(typeParameter);
log(parameterizedTypeParameter);
        this.parameterized = new Parameterized(parameterizedTypeParameter);
        gatherGenerics(this.parameterized.getType(), this.parameterized.getGenerics());
    }

    private static void gatherGenerics(java.lang.reflect.ParameterizedType parameterizedType, List<Type<?>> generics) {
        for (java.lang.reflect.Type type : parameterizedType.getActualTypeArguments()) {
            generics.add(new Type<Object>(type) {});
        }
    }

    private void log(Object... obj) {
        System.out.println(Arrays.toString(obj));
    }
}
