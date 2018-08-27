package com.sbnarra.inject.context;

public class ContextException extends Exception {
    public ContextException(String msg, Exception e) {
        super(msg, e);
    }

    public ContextException(String msg) {
        super(msg);
    }
}
