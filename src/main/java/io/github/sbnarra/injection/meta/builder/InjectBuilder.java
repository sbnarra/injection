package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.TypeBinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class InjectBuilder {

    public Meta.Inject build(AnnotatedElement annotatedElement) {
        return build(annotatedElement, null);
    }

    public <T> Meta.Inject build(AnnotatedElement annotatedElement, TypeBinding<T> binding) {
        Annotation qualifier = binding != null && binding.getQualifier() != null ?
                binding.getQualifier() : Annotations.findQualifier(annotatedElement);

        Annotation scope = binding != null && binding.getContract() != null && binding.getContract().getScoped() != null ?
                binding.getContract().getScoped() : Annotations.findScope(annotatedElement);

        return Meta.Inject.builder()
                .qualifier(qualifier)
                .scoped(scope)
                .build();
    }
}
