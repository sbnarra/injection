package io.github.sbnarra.injection.registry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
public abstract class Contract<
        GENERIC_BINDING extends Binding<?, ?, ?>,
        CONTRACT extends Contract<GENERIC_BINDING, CONTRACT, ACTUAL_BINDING>,
        ACTUAL_BINDING extends Binding<CONTRACT, GENERIC_BINDING, ACTUAL_BINDING>> {
    @ToString.Exclude private final ACTUAL_BINDING binding;
}
