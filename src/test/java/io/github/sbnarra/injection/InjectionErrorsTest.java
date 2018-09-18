package io.github.sbnarra.injection;

import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.RegistryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InjectionErrorsTest {

    @Test
    public void interfaceBinding() {
        try {
            InjectorFactory.create(new Registration() {
                @Override
                public void register() throws RegistryException {
                    bind(List.class).with(List.class);
                }
            });
            Assertions.fail("expecting error for interface contract");
        } catch (InjectException e) {
            Throwable t = getRootCause(e);
            Assertions.assertTrue(t.getMessage().endsWith("can't build an interface"));
        }
    }

    private Throwable getRootCause(Throwable e) {
        return e.getCause() != null ? getRootCause(e.getCause()) : e;
    }
}
