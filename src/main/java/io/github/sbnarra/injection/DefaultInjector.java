package io.github.sbnarra.injection;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.context.DefaultProvider;
import io.github.sbnarra.injection.type.Type;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.HashSet;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultInjector implements Injector {

    private final Context context;

    @Override
    public <T> T get(Type<T> type, Annotation qualifier, Annotation scope) throws InjectException {
        if (type.isProvider()) {
            return (T) new DefaultProvider<>(type.getParameterized().getGenerics().get(0), this, qualifier, scope);
        }

        try {
            return context.get(type, qualifier, scope, this, new HashSet<>());
        } catch (ContextException e) {
            throw new InjectException("failed to get: " + type, e);
        }
    }

    @Override
    public Context context() {
        return context;
    }
}
