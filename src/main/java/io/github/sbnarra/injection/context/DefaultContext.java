package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.context.graph.Graph;
import io.github.sbnarra.injection.context.graph.Node;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.misc.Buildability;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.RegistryException;
import io.github.sbnarra.injection.registry.TypeBinding;
import io.github.sbnarra.injection.type.Type;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DefaultContext implements Context {
    private final Registry registry;
    private final Graph graph;
    private final ScopedContext scopedContext;
    private final ObjectBuilder objectBuilder;

    @Override
    public <T> T get(Meta<T> meta, Meta.Inject inject, Injector injector) throws ContextException {
        if (inject.getScoped() != null) {
            return scopedContext.get(meta, inject, injector);
        } else if (meta.getClazz().getInject().getScoped() != null) {
            return scopedContext.get(meta, meta.getClazz().getInject(), injector);
        }
        return objectBuilder.construct(meta, injector);
    }

    @Override
    public ObjectBuilder objectBuilder() {
        return objectBuilder;
    }

    @Override
    public ScopedContext scopedContext() {
        return scopedContext;
    }

    @Override
    public Registry registry() {
        return registry;
    }

    @Override
    public <T> Meta<T> lookup(Type<T> theType, Annotation qualifier, Annotation scope, Set<Class<?>> staticsMembers) throws ContextException {
        Node<?> node = graph.find(theType,  qualifier);
        if (node != null) {
            return (Meta<T>) node.getMeta();
        }

        TypeBinding<?> newBinding;
        if (qualifier == null) {
            try {
                Buildability.check(theType);
            } catch (Buildability.Exception e) {
                throw new ContextException(e.getMessage());
            }
            newBinding = selfBinding(theType);
        } else {
            newBinding = registry.find(theType, qualifier);
            if (newBinding == null) {
                throw new ContextException("no binding found for: type=" + theType + ",qualifier=" + qualifier);
            }
        }
        return (Meta<T>) graph.addNode(newBinding, this, staticsMembers).getMeta();
    }

    private <T> TypeBinding<T> selfBinding(Type<T> theType) throws ContextException {
        try {
            return new TypeBinding<>(theType, registry().getTypeBindings())
                    .with(theType)
                    .getBinding();
        } catch (RegistryException e) {
            throw new ContextException("error creating self-binding: " + theType, e);
        }
    }


}
