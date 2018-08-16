package com.sbnarra.inject.meta;

import lombok.Builder;
import lombok.Value;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class AspectMeta {
    private final List<Method> methods = new ArrayList<>();
    private final InvocationHandler invocationHandler;
}
