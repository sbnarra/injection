package io.github.sbnarra.injection.core;

public class AnnotationsException extends Exception {
    public AnnotationsException(String msg) {
        super(msg);
    }

    public Unchecked unchecked() {
        return new Unchecked(this);
    }

    public class Unchecked extends RuntimeException {
        private final AnnotationsException e;

        private Unchecked(AnnotationsException e) {
            super(e);
            this.e = e;
        }

        public AnnotationsException annotationsException() {
            return e;
        }
    }
}
