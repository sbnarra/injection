package com.sbnarra.inject.core;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.inject.Named;
import java.lang.annotation.Annotation;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class NamedAnnotation implements Named {
    private final String value;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Named.class;
    }

    @Override
    public String value() {
        return value;
    }
}
