package io.github.sbnarra.injection.meta.builder;

public class BuilderException extends Exception {
    public BuilderException(String msg) {
        super(msg);
    }

    public BuilderException(String msg, Exception e) {
        super(msg, e);
    }

    public BuilderException.Unchecked unchecked() {
        return new BuilderException.Unchecked(this);
    }

    public class Unchecked extends RuntimeException {
        private final BuilderException e;

        private Unchecked(BuilderException e) {
            super(e);
            this.e = e;
        }

        public BuilderException builderException() {
            return e;
        }
    }
}
