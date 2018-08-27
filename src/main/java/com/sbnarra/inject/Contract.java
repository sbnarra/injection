package com.sbnarra.inject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
public abstract class Contract<B extends Binding> {
    @ToString.Exclude private final B binding;
}
