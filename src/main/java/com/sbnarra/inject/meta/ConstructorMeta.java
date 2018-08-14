package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Constructor;
import java.util.List;

@Value
@Builder
public class ConstructorMeta<T> {
    private final Constructor<T> constructor;
    private final List<ObjectMeta> fields;
}
