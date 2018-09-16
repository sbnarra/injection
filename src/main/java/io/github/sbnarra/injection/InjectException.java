package io.github.sbnarra.injection;

import io.github.sbnarra.injection.misc.UncheckedException;

public class InjectException extends UncheckedException {

    public InjectException(String msg, Exception e) {
        super(msg, e);
    }

    public InjectException(String msg) {
        super(msg);
    }
}
