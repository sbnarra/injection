package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.annotation.Annotations;
import io.github.sbnarra.injection.context.graph.Graph;
import io.github.sbnarra.injection.context.graph.GraphException;
import io.github.sbnarra.injection.meta.builder.MetaBuilderFactory;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.TypeBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ContextFactory {

    public Context create(Registry registry, Set<Class<?>> staticsMembers, Annotations annotations) throws ContextException {
        Graph graph = new Graph(new MetaBuilderFactory().newInstance(annotations));

        ScopedContext scopedContext = new ScopedContext(registry);
        ObjectBuilder objectBuilder = new DefaultObjectBuilder();
        Context context = new DefaultContext(registry, graph, scopedContext, objectBuilder);

        Iterator<TypeBinding<?>> typeBindingIterator = new ArrayList<>(registry.getTypeBindings()).iterator();
        while (typeBindingIterator.hasNext()) {
            TypeBinding<?> typeBinding = typeBindingIterator.next();
            try {
                graph.addNode(typeBinding, context, staticsMembers);
            } catch (GraphException e) {
                throw new ContextException("error adding type binding to graph: " + typeBinding, e);
            }
        }

        return context;
    }
}
