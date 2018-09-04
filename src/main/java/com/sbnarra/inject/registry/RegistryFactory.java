package com.sbnarra.inject.registry;

public class RegistryFactory {

    public static Registry registrate(Registration[] registrations) throws RegistryException {
        Registry registry = new Registry();
        for (Registration registration : registrations) {
            registration.setRegistry(registry);
            registration.register();
        }
        return registry;
    }
}
