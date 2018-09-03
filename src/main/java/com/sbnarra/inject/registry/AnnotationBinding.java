package com.sbnarra.inject.registry;

import com.sbnarra.inject.aspect.Aspect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class AnnotationBinding extends Binding<AnnotationContract, AnnotationBinding> {
    private final Class<? extends Annotation> annotationClass;

    public AnnotationBinding(Class<Annotation> annotationClass, List<AnnotationBinding> interceptionBindings) {
        super(interceptionBindings);
        this.annotationClass = annotationClass;
    }

    public AnnotationContract with(Aspect aspect) {
        return setContract(new AnnotationContract(this, aspect));
    }

    @Override
    protected void register(Collection<AnnotationBinding> registryBindings) {
        registryBindings.add(this);
    }
}
