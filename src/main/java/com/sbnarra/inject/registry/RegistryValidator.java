package com.sbnarra.inject.registry;

public class RegistryValidator {

    public void validate(Registry registry) throws RegistryException {
        for (TypeBinding<?> typeBinding : registry.getTypeBindings()) {
            if (typeBinding.getType() == null) {
                throw new RegistryException("");
            } else if (typeBinding.getInstance() == null && typeBinding.getContract() == null) {
                throw new RegistryException("missing contract/instance from binding: " + typeBinding);
            }
        }
    }
}
