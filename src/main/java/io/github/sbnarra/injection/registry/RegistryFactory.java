package io.github.sbnarra.injection.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RegistryFactory {

    public static Registry registrate(Registration[] registrationsArr) throws RegistryException {
        List<Registration> registrations = registrationsList(registrationsArr);

        Registry registry = new Registry();
        RegistryException errors = doRegistration(registrations, registry);

        if (errors.getSuppressed().length > 0) {
            throw errors;
        }
        return registry;
    }

    private static List<Registration> registrationsList(Registration[] registrationsArr) {
        List<Registration> registrations = new ArrayList<>();
        registrations.add(new DefaultRegistration());
        Stream.of(registrationsArr).forEach(r -> registrations.add(r));
        return registrations;
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
