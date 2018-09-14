package io.github.sbnarra.injection.context;

public class ContextException extends Exception {
    ContextException(String msg, Exception e) {
        super(msg, e);
    }

    ContextException(String msg) {
        super(msg);
    }

    public ContextException.Unchecked unchecked() {
        return new ContextException.Unchecked(this);
    }

    public class Unchecked extends RuntimeException {
        private final ContextException e;

        private Unchecked(ContextException e) {
            super(e);
            this.e = e;
        }

        public ContextException contextException() {
            return e;
        }
    }
}
