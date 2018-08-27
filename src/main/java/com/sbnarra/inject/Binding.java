package com.sbnarra.inject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class Binding<C extends Contract> {
    private C contract;

    protected C setContract(C contract) {
        return this.contract = contract;
    }
}
