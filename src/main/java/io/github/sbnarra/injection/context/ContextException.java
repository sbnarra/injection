package io.github.sbnarra.injection.context;

public class ContextException extends Exception {
    ContextException(String msg, Exception e) {
        super(msg, e);
    }

    ContextException(String msg) {
        super(msg);
    }
}
