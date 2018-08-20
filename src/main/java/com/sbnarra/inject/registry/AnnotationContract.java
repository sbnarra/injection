package com.sbnarra.inject.registry;

import com.sbnarra.inject.aspect.Aspect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class AnnotationContract extends Contract<AnnotationBinding> {
    private final Aspect aspect;

    public AnnotationContract(AnnotationBinding binding, Aspect aspect) {
        super(binding);
        this.aspect = aspect;
    }
}
