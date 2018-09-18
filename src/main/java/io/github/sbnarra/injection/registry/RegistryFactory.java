package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.annotation.Annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistryFactory {

    public static Registry registrate(Registration[] registrationsArr, Annotations annotations) throws RegistryException {
        List<Registration> registrations = new ArrayList<>(Arrays.asList(registrationsArr));
        registrations.add(new DefaultRegistration());

        Registry registry = new Registry(annotations);
        RegistryException errors = doRegistration(registrations, registry);

        if (errors.getSuppressed().length > 0) {
            throw errors;
        }
        return registry;
    }

    private static RegistryException doRegistration(List<Registration> registrations, Registry registry) {
        RegistryException errors = new RegistryException("registration errors");
        for (Registration registration : registrations) {
            registration.setRegistry(registry);
            try {
                registration.register();
            } catch (RegistryException e) {
                errors.addSuppressed(new RegistryException("error registering: " + registration, e));
            }
        }
        return errors;
    }
}
