package com.sbnarra.inject.registry;

import lombok.ToString;
import lombok.Value;

import java.lang.reflect.InvocationHandler;

@Value
public class InterceptionContract {
    @ToString.Exclude private final AnnotationBinding binding;
    private final InvocationHandler invocationHandler;
}
