package com.sbnarra.inject.meta.builder;

public class BuilderException extends Exception {
    public BuilderException(String msg) {
        super(msg);
    }

    public BuilderException(String msg, Exception e) {
        super(msg, e);
    }
}
