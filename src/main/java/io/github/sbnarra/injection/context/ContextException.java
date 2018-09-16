package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.core.UncheckedException;

public class ContextException extends UncheckedException {
    protected ContextException(String msg, Exception e) {
        super(msg, e);
    }

    protected ContextException(String msg) {
        super(msg);
    }
}
