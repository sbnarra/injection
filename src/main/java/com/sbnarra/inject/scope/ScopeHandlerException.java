package com.sbnarra.inject.scope;

import com.sbnarra.inject.context.ContextException;

public class ScopeHandlerException extends ContextException {
    public ScopeHandlerException(String msg, Exception e) {
        super(msg, e);
    }
}
