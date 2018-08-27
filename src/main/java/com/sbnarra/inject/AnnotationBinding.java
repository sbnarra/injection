package com.sbnarra.inject;

import com.sbnarra.inject.aspect.Aspect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class AnnotationBinding extends Binding<AnnotationContract> {
    private final Class<? extends Annotation> annotationClass;

    public AnnotationContract with(Aspect aspect) {
        return setContract(new AnnotationContract(this, aspect));
    }
}
