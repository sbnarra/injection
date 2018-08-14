package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClassMeta {
    private final Class<?> buildClass;
    private final Class<?> bindClass;
}
