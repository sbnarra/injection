package com.sbnarra.inject.registry;

import com.sbnarra.inject.aspect.Aspect;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.ParameterizedType;

@RequiredArgsConstructor
public class Register {

    private final Registration<Class<?>> classes;
    private final Registration<ParameterizedType> types;
    private final Registration<Aspect> aspects;
}
