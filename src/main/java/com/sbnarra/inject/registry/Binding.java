package com.sbnarra.inject.registry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@ToString
public class Binding<T> {
    @EqualsAndHashCode.Include private final Type<T> type;
    private Contract<T> contract;
    private String named;

    public Binding(Type<T> type) {
        this.type = type;
    }

    public Binding(Class<T> aClass) {
        this(new Type<T>(aClass) {});
    }

    public Contract<T> with(Type<? extends T> type) {
        return contract = new Contract<>(this, type);
    }

    public Contract<T> with(Class<? extends T> aClass) {
        return contract = new Contract<>(this, new Type<T>(aClass) {});
    }

    public Binding<T> named(String named) {
        this.named = named;
        return this;
    }
}
