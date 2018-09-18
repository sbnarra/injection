package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.aspect.Aspect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class AspectBinding extends Binding<AspectContract, AspectBinding, AspectBinding> {
    private final Class<?> annotationClass;

    public AspectBinding(Class<?> annotationClass, Collection<AspectBinding> interceptionBindings) {
        super(interceptionBindings);
        this.annotationClass = annotationClass;
    }

    public AspectContract with(Aspect aspect) throws RegistryException {
        return setContract(new AspectContract(this, aspect));
    }

    @Override
    protected void register(Collection<AspectBinding> registryBindings) throws RegistryException {
        if (!annotationClass.isAnnotation()) {
            throw new RegistryException(annotationClass + ": is not an annotation: use annotations to declare aspects");
        }
        registryBindings.add(this);
    }
}
