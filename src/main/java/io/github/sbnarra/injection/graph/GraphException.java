package io.github.sbnarra.injection.graph;

public class GraphException extends Exception {
    public GraphException(String msg, Exception e) {
        super(msg, e);
    }

    public GraphException(String msg) {
        super(msg);
    }
}
