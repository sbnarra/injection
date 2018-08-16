package com.sbnarra.inject.registry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public class AnnotationBinding {
    private final Class<? extends Annotation> annotationClass;
    private InterceptionContract interceptionContract;

    public InterceptionContract with(InvocationHandler invocationHandler) {
        return interceptionContract = new InterceptionContract(this, invocationHandler);
    }
}
