package com.sbnarra.inject.meta;

import lombok.Value;

import java.lang.reflect.Constructor;
import java.util.List;

@Value
public class ConstructorMeta<T> {
    private final Constructor<T> constructor;
    private final List<FieldMeta> fields;
}
