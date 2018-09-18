package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.misc.UncheckedException;

public class BuilderException extends UncheckedException {
    public BuilderException(String msg) {
        super(msg);
    }

    public BuilderException(String msg, Exception e) {
        super(msg, e);
    }
}
