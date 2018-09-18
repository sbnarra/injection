package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.annotation.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.TypeBinding;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

@RequiredArgsConstructor
public class InjectBuilder {
    private final Annotations annotations;

    public Meta.Inject build(AnnotatedElement annotatedElement) {
        return build(annotatedElement, null);
    }

    public <T> Meta.Inject build(AnnotatedElement annotatedElement, TypeBinding<T> binding) {
        Annotation qualifier = binding != null && binding.getQualifier() != null ?
                binding.getQualifier() : annotations.findQualifierAnnotation(annotatedElement);

        Annotation scope = binding != null && binding.getContract() != null && binding.getContract().getScoped() != null ?
                binding.getContract().getScoped() : annotations.findScopeAnnotation(annotatedElement);

        return Meta.Inject.builder()
                .qualifier(qualifier)
                .scoped(scope)
                .build();
    }
}
