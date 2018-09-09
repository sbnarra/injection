package io.github.sbnarra.injection.registry;

import io.github.sbnarra.injection.scope.ScopeHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.inject.Scope;
import java.util.Collection;
import java.util.stream.Stream;

@ToString(callSuper = true)
@Getter
@EqualsAndHashCode(callSuper = true)
public class ScopeBinding extends Binding<ScopeContract, ScopeBinding, ScopeBinding> {
    private final Class<?> scoped;

    public ScopeBinding(Class<?> scoped, Collection<ScopeBinding> registryBindings) {
        super(registryBindings);
        this.scoped = scoped;
    }

    public ScopeContract with(ScopeHandler scopeHandler) throws RegistryException {
        return setContract(new ScopeContract(this, scopeHandler));
    }

    @Override
    protected void register(Collection<ScopeBinding> registryBindings) throws RegistryException {
        if (!scoped.isAnnotation()) {
            throw new RegistryException(scoped + ": not an annotation: use annotations for scopes");
        }

        if (!Stream.of(scoped.getAnnotations()).anyMatch(a -> Scope.class.isInstance(a))) {
            throw new RegistryException(scoped + ": not annotated with " + Scope.class);
        }
        registryBindings.add(this);
    }
}
