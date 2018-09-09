package io.github.sbnarra.injection.core;

import io.github.sbnarra.injection.InjectException;
import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.context.ContextException;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
public class DefaultInjector implements Injector {

    private final Context context;

    @Override
    public <T> T get(Type<T> type, Annotation qualifier) throws InjectException {
        try {
            return context.get(type, qualifier);
        } catch (ContextException e) {
            throw new InjectException("failed to get: " + type, e);
        }
    }

    @Override
    public Context context() {
        return context;
    }
}
