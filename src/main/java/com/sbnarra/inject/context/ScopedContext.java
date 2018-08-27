package com.sbnarra.inject.context;

import com.sbnarra.inject.Registry;
import com.sbnarra.inject.ScopeBinding;
import com.sbnarra.inject.ScopeContract;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Scoped;
import com.sbnarra.inject.scope.ScopeHandler;
import com.sbnarra.inject.scope.ScopeHandlerException;
import com.sbnarra.inject.scope.SingletonScopeHandler;
import com.sbnarra.inject.scope.ThreadLocalScopeHandler;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ScopedContext {

    private final Map<List<Class<?>>, ScopeHandler> scopes = new HashMap<>();

    public ScopedContext(Registry registry, Annotations annotations) {
        scopes.put(annotations.getThreadLocal(), new ThreadLocalScopeHandler());
        scopes.put(annotations.getSingleton(), new SingletonScopeHandler());
        for (ScopeBinding binding : registry.getScopeBindings()) {
            Scoped scoped = binding.getScoped();
            ScopeContract contract = binding.getContract();

            scopes.put(scoped.getAnnotationClasses(), contract.getScopeHandler());
        }
    }

    public <T> T get(Meta<T> meta, Context context) throws ContextException {
        try {
            return getScopeHandler(meta).get(meta, context);
        } catch (ScopeHandlerException e) {
            throw new ContextException("error getting instance using scope handler", e);
        }
    }

    private <T> ScopeHandler getScopeHandler(Meta<T> meta) throws ContextException {
        Set<List<Class<?>>> keys = scopes.keySet();

        List<List<Class<?>>> matching = keys.stream()
                .filter(k -> k.contains(meta.getScoped()))
                .collect(Collectors.toList());

        if (matching.isEmpty()) {
            throw new ContextException("no handler found for scope: " + meta.getScoped() + ": " + scopes);
        } else if (matching.size() > 1) {
            throw new ContextException("multiple handlers found for scope, can only apply one: " + meta.getScoped() + ": " + matching);
        }
        return scopes.get(matching.get(0));
    }
}
