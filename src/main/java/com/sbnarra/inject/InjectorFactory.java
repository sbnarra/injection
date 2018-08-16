package com.sbnarra.inject;

import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.registry.Registration;
import com.sbnarra.inject.registry.Registry;

import java.util.Arrays;

public class InjectorFactory {

    public static Injector create(Registration... registrations) throws InjectException {
        return create(InjectionAnnotations.newInstance(), registrations);
    }

    public static Injector create(InjectionAnnotations injectionAnnotations, Registration... registrations) throws InjectException {
        Registry registry = Registry.doRegistrations(Arrays.asList(registrations));
        Graph graph = Graph.construct(registry, injectionAnnotations);
        return new DefaultInjector(graph);
    }
}
