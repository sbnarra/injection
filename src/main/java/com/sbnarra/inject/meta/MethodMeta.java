package com.sbnarra.inject.meta;

import lombok.Value;

import java.util.List;

@Value
public class MethodMeta {
    private final List<ObjectMeta> fields;
}
