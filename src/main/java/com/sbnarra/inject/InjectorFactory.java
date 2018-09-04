package com.sbnarra.inject;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.context.ContextFactory;
import com.sbnarra.inject.core.DefaultInjector;
import com.sbnarra.inject.registry.Registration;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.RegistryException;

import java.util.Arrays;

public class InjectorFactory {
    public static Injector create(Registration... registrations) throws InjectException {
        Registry registry = createRegistry(registrations);
        Context context = createContext(registry);
        return new DefaultInjector(context);
    }

    private static Registry createRegistry(Registration... registrations) throws InjectException {
        try {
            return Registry.registrate(registrations);
        } catch (RegistryException e) {
            throw new InjectException("error creating registry: " + Arrays.toString(registrations), e);
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
