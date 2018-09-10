package io.github.sbnarra.injection.core;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;

@ToString
@RequiredArgsConstructor
public class SimpleAnnotation implements Annotation {
    private final Class<? extends Annotation> annotation;

    @Override
    public Class<? extends Annotation> annotationType() {
        return annotation;
    }

    @Override
    public boolean equals(Object obj) {
        if (Annotation.class.isInstance(obj)) {
            Annotation annotation = Annotation.class.cast(obj);
            return annotationType().equals(annotation.annotationType());
        } else if (Class.class.isInstance(obj)) {
            return annotationType().equals(Class.class.cast(obj));
        }
        return false;
    }
}
