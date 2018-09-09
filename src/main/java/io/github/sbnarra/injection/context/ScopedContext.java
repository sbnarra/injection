package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.ScopeBinding;
import io.github.sbnarra.injection.registry.ScopeContract;
import io.github.sbnarra.injection.scope.ScopeHandler;
import io.github.sbnarra.injection.scope.ScopeHandlerException;
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

    public <T> T get(Meta<T> meta, DefaultContext context) throws ContextException {
        try {
            return getScopeHandler(meta).get(meta, context);
        } catch (ScopeHandlerException e) {
            throw new ContextException("error getting instance using scope handler", e);
        }
    }

    private <T> ScopeHandler getScopeHandler(Meta<T> meta) throws ContextException {
        Set<Class<?>> keys = scopes.keySet();

        Annotation scopeAnn = meta.getClazz().getInject().getScoped();
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

    public void destoryScope(Class<?> scopeClass) throws ScopeHandlerException {
        ScopeHandler scopeHandler = scopes.get(scopeClass);
        if (scopeHandler == null) {
            throw new ScopeHandlerException("no scope handler for: " + scopeClass);
        }
        scopeHandler.destoryScope();
    }
}
