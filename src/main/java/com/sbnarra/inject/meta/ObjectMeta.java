package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ObjectMeta {
    private final boolean isSingleton;
    private final String named;

    private final ClassMeta classMeta;
    private final ConstructorMeta constructorMeta;
    private final List<ObjectMeta> fieldMeta;
    private final List<MethodMeta> methodMeta;
    private final List<AspectMeta> aspectMeta;
}
