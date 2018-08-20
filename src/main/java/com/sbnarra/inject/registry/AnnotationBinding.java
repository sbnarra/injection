package com.sbnarra.inject.registry;

import com.sbnarra.inject.aspect.Aspect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public class AnnotationBinding extends Binding<AnnotationContract> {
    private final Class<? extends Annotation> annotationClass;
    private AnnotationContract interceptionContract;

    public AnnotationContract with(Aspect aspect) {
        return interceptionContract = new AnnotationContract(this, aspect);
    }
}
