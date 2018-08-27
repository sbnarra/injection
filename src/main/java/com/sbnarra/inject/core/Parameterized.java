package com.sbnarra.inject.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@Value
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Parameterized<T> {

    private final boolean isProvider;
    private final ParameterizedType type;
    private final List<Type<?>> generics = new ArrayList<Type<?>>();

    public java.lang.Class<T> getRawType() {
        return (java.lang.Class<T>) type.getRawType();
    }
}
