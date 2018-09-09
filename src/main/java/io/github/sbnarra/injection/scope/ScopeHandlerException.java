package io.github.sbnarra.injection.scope;

public class ScopeHandlerException extends Exception {
    public ScopeHandlerException(String msg, Exception e) {
        super(msg, e);
    }

    public ScopeHandlerException(String msg) {
        super(msg);
    }
}
