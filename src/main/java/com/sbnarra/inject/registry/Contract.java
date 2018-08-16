package com.sbnarra.inject.registry;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Contract<T> {
    private boolean asSingleton = false;

    @ToString.Exclude@EqualsAndHashCode.Exclude
    private final Binding<T> binding;
    private final Type<? extends T> type;
    private final T instance;

    public Contract(Binding<T> binding, Type<? extends T> aType) {
        this(binding, aType, null);
    }

    public Contract(Binding<T> binding, T instance) {
        this(binding, null, instance);
    }
}
