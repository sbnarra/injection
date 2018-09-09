package io.github.sbnarra.injection;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.context.ContextFactory;
import io.github.sbnarra.injection.core.DefaultInjector;
import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.RegistryException;
import io.github.sbnarra.injection.registry.RegistryFactory;

public class InjectorFactory {
    public static Injector create(Registration... registrations) throws InjectException {
        Registry registry = createRegistry(registrations);
        Context context = createContext(registry);
        return new DefaultInjector(context);
    }

    private static Registry createRegistry(Registration[] registrations) throws InjectException {
        try {
            return RegistryFactory.registrate(registrations);
        } catch (RegistryException e) {
            throw new InjectException("error creating registry", e);
        }
    }

    private static Context createContext(Registry registry) throws InjectException {
        try {
            return new ContextFactory().create(registry);
        } catch (ContextException e) {
            throw new InjectException("error creating injection context", e);
        }
    }
}
