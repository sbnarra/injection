package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

import java.lang.reflect.Method;
import java.util.List;

@Value
@Builder
public class MethodMeta {
    private final Method method;
    private final List<ObjectMeta> fields;
}
