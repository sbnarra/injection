package com.sbnarra.inject.meta;

import lombok.Value;

public interface Qualifier {
    @Value
    class Annotated implements Qualifier {
        private final Class<?> qualifier;
    }

    @Value
    class Named implements Qualifier {
        private final String name;
    }
}
