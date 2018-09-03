package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.TypeBinding;

import javax.inject.Qualifier;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InjectBuilder extends AbstractBuilder {

    public InjectBuilder(Annotations annotations) {
        super(annotations);
    }

    public Meta.Inject build(AnnotatedElement annotatedElement) throws BuilderException {
        return build(annotatedElement, null);
    }

    public <T> Meta.Inject build(AnnotatedElement annotatedElement, TypeBinding<T> binding) throws BuilderException {
        Annotation qualifier = binding != null && binding.getQualifier() != null ?
                binding.getQualifier() : findAnnotation(annotatedElement, Qualifier.class);

        Annotation scope = binding != null && binding.getContract() != null && binding.getContract().getScoped() != null ?
                binding.getContract().getScoped() : findAnnotation(annotatedElement, Scope.class);

        return Meta.Inject.builder()
                .qualifier(qualifier)
                .scoped(scope)
                .build();
    }

    private Annotation findAnnotation(AnnotatedElement annotatedElement, Class<?> annotationClass) throws BuilderException {
        List<Annotation> annotations = new ArrayList<>();
        for (Annotation elementAnnotation : annotatedElement.getAnnotations()) {
            if (Stream.of(elementAnnotation.annotationType().getAnnotations())
                    .anyMatch(a -> annotationClass.isAssignableFrom(a.annotationType()))) {
                annotations.add(elementAnnotation);
            }
        }

        if (annotations.isEmpty()) {
            return null;
        } else if (annotations.size() > 1) {
            throw new BuilderException("multiple " + annotationClass + ": " + annotations);
        }
        return annotations.get(0);
    }
}
