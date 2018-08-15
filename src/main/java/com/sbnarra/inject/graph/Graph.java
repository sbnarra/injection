package com.sbnarra.inject.graph;

import com.sbnarra.inject.aspect.Aspect;
import com.sbnarra.inject.meta.builder.ObjectMetaBuilder;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.binding.Binding;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Graph {

    private final List<DependencyNode> roots;
    private final ObjectMetaBuilder objectMetaBuilder;

    public Graph construct(Registry registry) {
        Graph graph = new Graph(new ArrayList<>(), (ObjectMetaBuilder)null);

        List<Aspect> aspects = registry.getAspects();

        List<Binding> bindings = registry.getBindings();
        

        return graph;
    }
}
