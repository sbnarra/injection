package com.sbnarra.inject.registry;

import lombok.Getter;

import java.lang.reflect.ParameterizedType;

@Getter
public class Type<T> {

    private final ParameterizedType type;

    public Type() {
        this.type = cast(getClass().getGenericSuperclass());
    }

    private static ParameterizedType cast(java.lang.reflect.Type type) {
        if (ParameterizedType.class.isInstance(type)) {
            return ParameterizedType.class.cast(type);
        }
        throw new RuntimeException("type isn't parameterized: " + type.getClass());
    }
}
