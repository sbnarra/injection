package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.ThreadLocal;
import io.github.sbnarra.injection.scope.SingletonScopeHandler;
import io.github.sbnarra.injection.scope.ThreadLocalScopeHandler;

import javax.inject.Singleton;

class DefaultRegistration extends Registration {
    @Override
    public void register() throws RegistryException {
        scoped(ThreadLocal.class).with(new ThreadLocalScopeHandler());
        scoped(Singleton.class).with(new SingletonScopeHandler());
    }
}
