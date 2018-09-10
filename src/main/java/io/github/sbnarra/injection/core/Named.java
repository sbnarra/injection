package io.github.sbnarra.injection.core;

import lombok.ToString;

@ToString
public class Named extends SimpleAnnotation implements javax.inject.Named {
    private final String named;

    public Named(String named) {
        super(javax.inject.Named.class);
        this.named = named;
    }

    @Override
    public String value() {
        return named;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj) || !javax.inject.Named.class.isInstance(obj)) {
            return false;
        }
        return named.equals(javax.inject.Named.class.cast(obj).value());
    }
}
