package io.github.sbnarra.injection;

public class InjectException extends Exception {

    public InjectException(String msg, Exception e) {
        super(msg, e);
    }

    public Unchecked unchecked() {
        return new Unchecked(this);
    }

    public class Unchecked extends RuntimeException {
        private final InjectException e;

        private Unchecked(InjectException e) {
            super(e);
            this.e = e;
        }

        public InjectException injectException() {
            return e;
        }
    }
}
