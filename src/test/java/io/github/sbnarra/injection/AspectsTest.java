package io.github.sbnarra.injection;

import io.github.sbnarra.injection.aspect.Aspect;
import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.RegistryException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicBoolean;

public class AspectsTest {

    @Test
    public void interceptedMethodTest() throws InjectException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Aspect testAspect = (proxy, method, invoker, args) -> {
            atomicBoolean.set(true);
            return invoker.invoke(args);
        };

        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() throws RegistryException {
                intercept(Intercept.class).with(testAspect);
            }
        });

        Assertions.assertFalse(atomicBoolean.get());
        injector.get(InterceptedObject.class).methodCall();
        Assertions.assertTrue(atomicBoolean.get());
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Intercept {}
    public static class InterceptedObject {
        @Intercept
        public void methodCall() {}
    }
}
