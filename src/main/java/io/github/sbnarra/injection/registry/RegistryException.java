package io.github.sbnarra.injection.registry;

public class RegistryException extends Exception {
    public RegistryException(String msg) {
        super(msg);
    }

    public RegistryException(String msg, Exception e) {
        super(msg, e);
    }
}
