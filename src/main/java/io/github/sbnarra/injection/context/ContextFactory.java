package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.graph.Graph;
import io.github.sbnarra.injection.graph.GraphException;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.meta.builder.MetaBuilderFactory;
import io.github.sbnarra.injection.registry.Registry;
import io.github.sbnarra.injection.registry.TypeBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContextFactory {

    public Context create(Registry registry) throws ContextException {
        List<Meta.Field> staticFieldMetas = new ArrayList<>();
        List<Meta.Method> staticMethodMetas = new ArrayList<>();

        Graph graph = new Graph(new MetaBuilderFactory().newInstance(), staticFieldMetas, staticMethodMetas);

        ScopedContext scopedContext = new ScopedContext(registry);
        Context context = new DefaultContext(registry, graph, scopedContext, staticFieldMetas, staticMethodMetas);

        Iterator<TypeBinding<?>> typeBindingIterator = new ArrayList<>(registry.getTypeBindings()).iterator();
        while (typeBindingIterator.hasNext()) {
            TypeBinding<?> typeBinding = typeBindingIterator.next();
            try {
                graph.addNode(typeBinding, context);
            } catch (GraphException e) {
                throw new ContextException("error adding type binding to graph: " + typeBinding, e);
            }
        }

        return context;
    }
}
