package com.sbnarra.inject.context;

import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.graph.GraphException;
import com.sbnarra.inject.meta.builder.MetaBuilderFactory;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.TypeBinding;

public class ContextFactory {

    public Context create(Registry registry, Annotations annotations) throws ContextException {
        Graph graph = new Graph(new MetaBuilderFactory().newInstance(annotations));

        ScopedContext scopedContext = new ScopedContext(registry, annotations);
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
