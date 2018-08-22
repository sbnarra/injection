package com.sbnarra.inject;

import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.Context;
import com.sbnarra.inject.core.DefaultInjector;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.registry.Registration;
import com.sbnarra.inject.registry.Registry;

public class InjectorFactory {

    public static Injector create(Registration... registrations) throws InjectException {
        return create(Annotations.newInstance(), registrations);
    }

    public static Injector create(Annotations annotations, Registration... registrations) throws InjectException {
        Registry registry = Registry.registrate(registrations);
        Context context = Graph.construct(registry, annotations);
        return new DefaultInjector(context);
    }
}
