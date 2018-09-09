package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.InjectException;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import javax.inject.Provider;

@RequiredArgsConstructor
public class DefaultProvider<T> implements Provider<T> {
    private final Meta<T> meta;
    private final Context context;

    @Override
    public T get() {
        try {
            return context.get(meta);
        } catch (ContextException e) {
            throw new InjectException("failed to provide: " + meta, e).unchecked();
        }
    }
}
