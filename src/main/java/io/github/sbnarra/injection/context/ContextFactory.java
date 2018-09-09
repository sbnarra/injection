package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.graph.Graph;
import io.github.sbnarra.injection.graph.GraphException;
import io.github.sbnarra.injection.meta.builder.MetaBuilderFactory;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.TypeBinding;

public class ContextFactory {

    public Context create(Registry registry) throws ContextException {
        Graph graph = new Graph(new MetaBuilderFactory().newInstance());

        ScopedContext scopedContext = new ScopedContext(registry);
        Context context = new DefaultContext(registry, graph, scopedContext);

        for (TypeBinding<?> typeBinding : registry.getTypeBindings()) {
            try {
                graph.addNode(typeBinding, context);
            } catch (GraphException e) {
                throw new ContextException("error adding type binding to graph: " + typeBinding, e);
            }
        }

        return context;
    }
}
