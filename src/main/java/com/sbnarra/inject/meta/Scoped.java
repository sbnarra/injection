package com.sbnarra.inject.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class Scoped {

    private final List<Class<?>> annotationClasses;

    public Scoped(Class<?>... annotationClasses) {
        this(Arrays.asList(annotationClasses));
    }

    public @interface Singleton {}
    public @interface ThreadLocal {}
}
