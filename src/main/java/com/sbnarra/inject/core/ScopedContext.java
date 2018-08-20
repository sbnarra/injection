package com.sbnarra.inject.core;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Scoped;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.ScopeBinding;
import com.sbnarra.inject.registry.ScopeContract;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ScopedContext {

    private final Map<List<Class<Annotation>>, ScopeHandler> scopes = new HashMap<>();

    public ScopedContext(Registry registry, Annotations annotations) {
        scopes.put(annotations.getThreadLocal(), new ThreadLocalScopeHandler());
        scopes.put(annotations.getSingleton(), new SingletonScopeHandler());
        for (ScopeBinding binding : registry.getScopeBindings()) {
            Scoped scoped = binding.getScoped();
            ScopeContract contract = binding.getContract();

            scopes.put(scoped.getAnnotationClasses(), contract.getScopeHandler());
        }
        Debug.log("created scope context: " + scopes);
    }

    public <T> T get(Meta<T> meta, Context context) throws InjectException {
        return getScopeHandler(meta).get(meta, context);
    }

    private <T> ScopeHandler getScopeHandler(Meta<T> meta) throws InjectException {
        Set<List<Class<Annotation>>> keys = scopes.keySet();

        List<List<Class<Annotation>>> matching = keys.stream()
                .filter(k -> k.contains(meta.getScoped()))
                .collect(Collectors.toList());

        Debug.log(matching);
        if (matching.isEmpty()) {
            throw new InjectException("no handler found for scope: " + meta.getScoped() + ": " + scopes);
        } else if (matching.size() > 1) {
            throw new InjectException("multiple handlers found for scope, can only apply one: " + meta.getScoped() + ": " + matching);
        }
        return scopes.get(matching.get(0));
    }
}
