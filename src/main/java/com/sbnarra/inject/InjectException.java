package com.sbnarra.inject;

public class InjectException extends Exception {
    public InjectException(String msg) {
        super(msg);
    }

    public InjectException(String msg, Exception e) {
        super(msg, e);
    }
}
