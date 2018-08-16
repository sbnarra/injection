package com.sbnarra.inject.meta;

import lombok.Value;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Value
public class AnnotationAspectMeta {
    private final InvocationHandler invocationHandler;
    private final List<Method> methods;

}
