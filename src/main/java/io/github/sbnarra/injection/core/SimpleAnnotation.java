package io.github.sbnarra.injection.core;

import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
public class SimpleAnnotation implements Annotation {
    private final Class<? extends Annotation> annotationType;

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotationType;
    }
}
