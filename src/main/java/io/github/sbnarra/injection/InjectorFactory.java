package io.github.sbnarra.injection;

import io.github.sbnarra.injection.annotation.Annotations;
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
        return create(Annotations.newDefault(), registrations);
    }

    public static Injector create(Annotations annotations, Registration... registrations) throws InjectException {
        return create(annotations, new StaticContext(annotations), registrations);
    }

    public static Injector create(Annotations annotations, StaticContext staticContext, Registration... registrations) throws InjectException {
        Registry registry = createRegistry(registrations, annotations);

        Set<Class<?>> staticsMembers = new HashSet<>();
        Context context = createContext(registry, staticsMembers, annotations);

        Injector injector = new DefaultInjector(context);
        if (staticContext != null) {
            injectStatics(staticContext, staticsMembers, injector);
        }
        return injector;
    }

    private static void injectStatics(StaticContext staticContext, Set<Class<?>> staticsMembers, Injector injector) throws InjectException {
        try {
            staticContext.inject(staticsMembers, injector);
        } catch (ContextException e) {
            throw new InjectException("error injecting static members", e);
        }
    }

    private static Registry createRegistry(Registration[] registrations, Annotations annotations) throws InjectException {
        try {
            return RegistryFactory.registrate(registrations, annotations);
        } catch (RegistryException e) {
            throw new InjectException("error creating registry", e);
        }
    }

    private static Context createContext(Registry registry, Set<Class<?>> staticsMembers, Annotations annotations) throws InjectException {
        try {
            return new ContextFactory().create(registry, staticsMembers, annotations);
        } catch (ContextException e) {
            throw new InjectException("error creating injection context", e);
        }
    }
}
