package com.sbnarra.inject.meta;

import com.sbnarra.inject.registry.Type;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ClassMeta {
    private final boolean isSingleton;
    private final Class<?> buildWith;
    private final Class<?> baseClass;

    private final ConstructorMeta constructorMeta;
    private final List<FieldMeta> fieldMeta;
    private final List<MethodMeta> methodMeta;
}
