package io.github.sbnarra.injection.context.graph;

import io.github.sbnarra.injection.context.ContextException;

public class GraphException extends ContextException {
    protected GraphException(String msg, Exception e) {
        super(msg, e);
    }

    public GraphException(String msg) {
        super(msg);
    }
}
