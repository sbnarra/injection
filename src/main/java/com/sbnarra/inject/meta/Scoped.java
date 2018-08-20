package com.sbnarra.inject.meta;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public abstract class Scoped {

    private final List<Class<Annotation>> annotationClasses;

    public @interface Singleton {}
    public static class SingletonScope extends Scoped {
        public SingletonScope(Class<Annotation>... annotationClasses) {
            super(Arrays.asList(annotationClasses));
        }

        public SingletonScope(List<Class<Annotation>> annotationClasses) {
            super(annotationClasses);
        }
    }

    public @interface ThreadLocal {}
    public static class ThreadLocalScope extends Scoped {
        public ThreadLocalScope(Class<Annotation>... annotationClasses) {
            super(Arrays.asList(annotationClasses));
        }

        public ThreadLocalScope(List<Class<Annotation>> annotationClasses) {
            super(annotationClasses);
        }
    }
}
