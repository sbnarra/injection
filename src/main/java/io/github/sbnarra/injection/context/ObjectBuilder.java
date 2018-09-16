package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.meta.Meta;

public interface ObjectBuilder {
    <T> T construct(Meta<T> meta, Injector injector) throws ContextException ;
}
