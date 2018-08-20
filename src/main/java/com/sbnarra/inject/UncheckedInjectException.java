package com.sbnarra.inject;

public class UncheckedInjectException extends RuntimeException {

    public UncheckedInjectException(InjectException e) {
        super(e);
    }
}
