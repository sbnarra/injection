package com.sbnarra.inject.context;

import com.sbnarra.inject.ThreadLocal;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.ScopeBinding;
import com.sbnarra.inject.registry.ScopeContract;
import com.sbnarra.inject.scope.ScopeHandler;
import com.sbnarra.inject.scope.ScopeHandlerException;
import com.sbnarra.inject.scope.SingletonScopeHandler;
import com.sbnarra.inject.scope.ThreadLocalScopeHandler;
import lombok.RequiredArgsConstructor;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ScopedContext {

    private final Map<Class<?>, ScopeHandler> scopes = new HashMap<>();

    ScopedContext(Registry registry) {
        scopes.put(ThreadLocal.class, new ThreadLocalScopeHandler());
        scopes.put(Singleton.class, new SingletonScopeHandler());
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

        List<Class<?>> matching = keys.stream()
                .filter(k -> k.isAssignableFrom(meta.getClazz().getInject().getScoped().annotationType()))
                .collect(Collectors.toList());
        if (matching.isEmpty()) {
            throw new ContextException("no handler found for scope: " + meta.getClazz().getInject().getScoped().annotationType() + ": scopes:" + scopes);
        } else if (matching.size() > 1) {
            throw new ContextException("multiple handlers found for scope, can only apply one: " + meta.getClazz().getInject().getScoped() + ": " + matching);
        }
        return scopes.get(matching.get(0));
    }
}
