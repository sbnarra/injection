package com.sbnarra.inject.graph;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.InjectionAnnotations;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.meta.builder.ObjectMetaBuilder;
import com.sbnarra.inject.meta.builder.ObjectMetaBuilderFactory;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@ToString
public class Graph {

    private final Set<DependencyNode> rootNodes = new HashSet<>();
    private final ObjectMetaBuilder objectMetaBuilder;

    public static Graph construct(Registry registry, InjectionAnnotations injectionAnnotations) throws InjectException {
        Graph graph = new Graph(new ObjectMetaBuilderFactory(injectionAnnotations).get());
        for (Binding<?> binding : registry.getBindings()) {
            graph.addNode(binding, registry);
        }
        return graph;
    }

    public DependencyNode addNode(Binding<?> binding, Registry registry) throws InjectException {
        DependencyNode dependencyNode = find(binding.getType(), binding.getNamed());
        if (dependencyNode != null) {
            return dependencyNode;
        }

        ObjectMeta objectMeta = objectMetaBuilder.build(binding, this, registry);
        rootNodes.add(dependencyNode = new DependencyNode(objectMeta));
        return dependencyNode;
    }

    public <T> DependencyNode find(Type<T> type, String named) {
        if (type.getParameterized() != null) {
            return find(type.getParameterized().getRawType(), named, rootNodes);
        }
        return find(type.getClazz().getTheClass(), named, rootNodes);
    }

    public <T> DependencyNode find(Class<T> tClass, String named) {
        return find(tClass, named, rootNodes);
    }

    public <T> DependencyNode find(Class<T> tClass, String named, Set<DependencyNode> nodes) {
        for (DependencyNode dependencyNode : nodes) {
            ObjectMeta objectMeta = dependencyNode.getObjectMeta();
            ClassMeta classMeta = objectMeta.getClassMeta();

            Class<?> bindClass = classMeta.getBindClass();
            if (tClass.equals(bindClass)) {
                return dependencyNode;
            }

            DependencyNode foundDependencyNode = find(tClass, named, dependencyNode.getDescendants());
            if (foundDependencyNode != null) {
                return foundDependencyNode;
            }
        }
        return null;
    }
}
