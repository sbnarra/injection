package com.sbnarra.inject.context;

public class ContextException extends Exception {
    ContextException(String msg, Exception e) {
        super(msg, e);
    }

    ContextException(String msg) {
        super(msg);
    }
}
