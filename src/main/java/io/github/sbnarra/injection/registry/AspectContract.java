package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.aspect.Aspect;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AspectContract extends Contract<AspectBinding, AspectContract, AspectBinding> {
    private final Aspect aspect;

    public AspectContract(AspectBinding binding, Aspect aspect) {
        super(binding);
        this.aspect = aspect;
    }
}
