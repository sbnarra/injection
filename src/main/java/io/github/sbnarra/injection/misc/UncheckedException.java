package io.github.sbnarra.injection.misc;

public class UncheckedException extends Exception {

    protected UncheckedException(String msg, Exception e) {
        super(msg, e);
    }

    protected UncheckedException(String msg) {
        super(msg);
    }

    public <T extends Exception> Unchecked unchecked() {
        return new Unchecked(this);
    }

    public class Unchecked extends RuntimeException {
        private UncheckedException exception;

        private Unchecked(UncheckedException exception) {
            super(exception);
            this.exception = exception;
        }


        public <T extends UncheckedException> T checked(Class<T> exceptionClass) {
            return exceptionClass.cast(exception);
        }
    }
}
