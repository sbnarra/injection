package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;

@Value
@Builder
public class AspectMeta {
    private final Class<? extends Annotation> annotationClass;
    private final InvocationHandler invocationHandler;
}
