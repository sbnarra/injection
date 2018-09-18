package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.annotation.Annotations;
import io.github.sbnarra.injection.context.graph.Graph;
import io.github.sbnarra.injection.context.graph.GraphException;
import io.github.sbnarra.injection.meta.builder.MetaBuilderFactory;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.TypeBinding;

import java.util.ArrayList;
import java.util.Set;

public class ContextFactory {

    public Context create(Registry registry, Set<Class<?>> staticsMembers, Annotations annotations) throws ContextException {
        return create(registry, staticsMembers, new MetaBuilderFactory(annotations));
    }

    public Context create(Registry registry, Set<Class<?>> staticsMembers, MetaBuilderFactory metaBuilderFactory) throws ContextException {
        ScopedContext scopedContext = new ScopedContext(registry);
        ObjectBuilder objectBuilder = new DefaultObjectBuilder();
        Graph graph = new Graph(metaBuilderFactory.newInstance());
        Context context = new DefaultContext(registry, graph, scopedContext, objectBuilder);
        buildGraph(graph, registry, context, staticsMembers);
        return context;
    }

    private void buildGraph(Graph graph, Registry registry, Context context, Set<Class<?>> staticsMembers) throws ContextException {
        for (TypeBinding<?> typeBinding : new ArrayList<>(registry.getTypeBindings())) {
            try {
                graph.addNode(typeBinding, context, staticsMembers);
            } catch (GraphException e) {
                throw new ContextException("error adding type binding to graph: " + typeBinding, e);
            }
        }
    }
}
