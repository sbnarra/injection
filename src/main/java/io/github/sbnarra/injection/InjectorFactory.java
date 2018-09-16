package io.github.sbnarra.injection;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.context.ContextFactory;
import io.github.sbnarra.injection.context.StaticContext;
import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.RegistryException;
import io.github.sbnarra.injection.registry.RegistryFactory;

import java.util.HashSet;
import java.util.Set;

public class InjectorFactory {
    public static Injector create(Registration... registrations) throws InjectException {
        Registry registry = createRegistry(registrations);

        Set<Class<?>> staticsMembers = new HashSet<>();
        Context context = createContext(registry, staticsMembers);

        Injector injector = new DefaultInjector(context);
        try {
            StaticContext.inject(staticsMembers, injector);
        } catch (ContextException e) {
            throw new InjectException("error injecting static members", e);
        }
        return injector;
    }

    private static Registry createRegistry(Registration[] registrations) throws InjectException {
        try {
            return RegistryFactory.registrate(registrations);
        } catch (RegistryException e) {
            throw new InjectException("error creating registry", e);
        }
    }

    private static Context createContext(Registry registry, Set<Class<?>> staticsMembers) throws InjectException {
        try {
            return new ContextFactory().create(registry, staticsMembers);
        } catch (ContextException e) {
            throw new InjectException("error creating injection context", e);
        }
    }
}
