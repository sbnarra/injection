package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.scope.ScopeHandler;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.ScopeBinding;
import io.github.sbnarra.injection.registry.ScopeContract;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ScopedContext {

    private final Map<Class<?>, ScopeHandler> scopes = new HashMap<>();

    ScopedContext(Registry registry) {
        for (ScopeBinding binding : registry.getScopeBindings()) {
            Class<?> scoped = binding.getScoped();
            ScopeContract contract = binding.getContract();
            scopes.put(scoped, contract.getScopeHandler());
        }
    }

    public <T> T get(Meta<T> meta, Meta.Inject inject, Injector injector) throws ContextException {
        return getScopeHandler(meta, inject).get(meta, inject, injector);
    }

    private <T> ScopeHandler getScopeHandler(Meta<T> meta, Meta.Inject inject) throws ContextException {
        Set<Class<?>> keys = scopes.keySet();

        Annotation scopeAnn = inject.getScoped();
        Class<? extends Annotation> annotationType = scopeAnn.annotationType();

        List<Class<?>> matching = keys.stream()
                .filter(annotationType::isAssignableFrom)
                .collect(Collectors.toList());
        if (matching.isEmpty()) {
            throw new ContextException("no handler found for scope: " + annotationType + ": scopes:" + scopes);
        } else if (matching.size() > 1) {
            throw new ContextException("multiple handlers found for scope, can only apply one: " + scopeAnn + ": " + matching);
        }
        return scopes.get(matching.get(0));
    }

    public void destoryScope(Class<?> scopeClass) throws ContextException {
        ScopeHandler scopeHandler = scopes.get(scopeClass);
        if (scopeHandler == null) {
            throw new ContextException("no scope handler for: " + scopeClass);
        }
        scopeHandler.destoryScope();
    }
}
