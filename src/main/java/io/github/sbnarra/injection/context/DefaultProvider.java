package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.InjectException;
import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.type.Type;
import lombok.RequiredArgsConstructor;

import javax.inject.Provider;
import java.lang.annotation.Annotation;

@RequiredArgsConstructor
public class DefaultProvider<T> implements Provider<T> {
    private final Type<T> type;
    private final Injector injector;
    private final Annotation qualifier;
    private final Annotation scope;

    @Override
    public T get() {
        try {
            return injector.get(type, qualifier, scope);
        } catch (InjectException e) {
            throw e.unchecked();
        }
    }
}
