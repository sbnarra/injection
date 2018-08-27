package com.sbnarra.inject.meta;

import lombok.NonNull;
import lombok.Value;

public interface Qualifier {
    @Value
    class Annotated implements Qualifier {
        @NonNull private final Class<?> qualifier;
    }

    @Value
    class Named implements Qualifier {
        private final String name;
    }
}
