package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.core.Annotations;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class AbstractBuilder {
    private static final Class<?> abstractInject = Inject.class;
    private static final Class<Annotation> inject = (Class<Annotation>) abstractInject;
    private final Annotations annotations;

    protected Annotations annotations() {
        return annotations;
    }

    protected <T extends AnnotatedElement> List<T> findScoped(T... executables) {
        Class<Annotation>[] annotationClasses = annotations.getScope().toArray(new Class[0]);
        return findList(executables, (f, l) -> l.add(f.getExecutable()), annotationClasses);
    }

    protected <T extends AnnotatedElement> Map<T, Annotation> findQualified(T... executables) throws BuilderException {
        try {
            return findMap(executables, annotations.getQualifier(), (f, m) -> {
                if (f.getAnnotations().size() > 1) {
                    throw new UncheckedBuilderException(
                            new BuilderException("multiple qualifiers found: " + f.getExecutable() + ": " + f.getAnnotations()));
                } else if (f.getAnnotations().size() == 1) {
                    m.put(f.getExecutable(), f.getAnnotations().get(0));
                }
            });
        } catch (UncheckedBuilderException e) {
            throw e.builderException();
        }
    }


    private <T extends AnnotatedElement> Map<T, Annotation> findMap(
            T[] annotatedElements, List<Class<Annotation>> annotationClasses, BiConsumer<FoundAnnotations<T>, Map<T, Annotation>> mapper) throws BuilderException {
        Map<T, Annotation> found = new HashMap<>();
        for (int i = 0; i < annotatedElements.length; i++) {
            FoundAnnotations<T> foundAnnotations = findAnnotations(annotatedElements[i], i, annotationClasses);
            if (foundAnnotations != null) {
                try {
                    mapper.accept(foundAnnotations, found);
                } catch (UncheckedBuilderException e) {
                    throw e.builderException();
                }
            }
        }
        return found;
    }

    protected <T extends AnnotatedElement> List<T> findInject(T... annotatedElements) {
        return findList(annotatedElements, (f, l) -> l.add(f.getExecutable()), inject);
    }

    protected <T extends AnnotatedElement> List<Integer> findInjectIndexes(T... executables) {
        return findList(executables, (f, l) -> l.add(f.getIndex()), inject);
    }

    private <R, T extends AnnotatedElement> List<R> findList(
            T[] annotatedElements, BiConsumer<FoundAnnotations<T>, List<R>> mapper, Class<Annotation>... annotationClasses) {
        List<R> found = new ArrayList<>();
        for (int i = 0; i < annotatedElements.length; i++) {
            for (Class<Annotation> annotationClass : annotationClasses) {
                FoundAnnotations<T> foundAnnotations = findAnnotation(null, i, annotatedElements[i], annotationClass);
                if (foundAnnotations != null) {
                    mapper.accept(foundAnnotations, found);
                }
            }
        }
        return found;
    }

    private <T extends AnnotatedElement> FoundAnnotations<T> findAnnotations(T annotatedElement, int i, List<Class<Annotation>> annotationClasses) {
        FoundAnnotations<T> foundAnnotations = null;
        for (Class<Annotation> annotationClass : annotationClasses) {
            foundAnnotations = findAnnotation(foundAnnotations, i, annotatedElement, annotationClass);
        }
        return foundAnnotations;
    }

    private <T extends AnnotatedElement> FoundAnnotations<T> findAnnotation(FoundAnnotations<T> foundAnnotations, int i, T annotatedElement, Class<Annotation> annotationClass) {
        Annotation annotation = annotatedElement.getAnnotation(annotationClass);
        if (annotation != null) {
            if (foundAnnotations == null) {
                foundAnnotations = new FoundAnnotations<>(annotatedElement, i);
            }
            foundAnnotations.getAnnotations().add(annotation);
        }
        return foundAnnotations;
    }

    @Value
    @Builder
    private static class FoundAnnotations<T extends AnnotatedElement> {
        private final T executable;
        private final Integer index;
        private final List<Annotation> annotations = new ArrayList<>();
    }

    private class UncheckedBuilderException extends RuntimeException {
        private final BuilderException e;
        public UncheckedBuilderException(BuilderException e) {
            super(e);
            this.e = e;
        }

        public BuilderException builderException() {
            return e;
        }
    }
}
