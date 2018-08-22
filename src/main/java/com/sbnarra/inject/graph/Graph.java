package com.sbnarra.inject.graph;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.Context;
import com.sbnarra.inject.core.ScopedContext;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.meta.builder.MetaBuilder;
import com.sbnarra.inject.meta.builder.MetaBuilderFactory;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.TypeBinding;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@ToString
public class Graph {

    @Value
    public class Node<T> {
        private final Set<Node> ancestors = new HashSet<>();
        private final Meta<T> meta;
        private final Set<Node> descendants = new HashSet<>();
    }

    private final Set<Node> rootNodes = new HashSet<>();
    private final MetaBuilder metaBuilder;

    public static Context construct(Registry registry, Annotations annotations) throws InjectException {
        Graph graph = new Graph(new MetaBuilderFactory().newInstance(annotations));
        ScopedContext scopedContext = new ScopedContext(registry, annotations);
        Context context = new Context(registry, graph, scopedContext);
        for (TypeBinding<?> typeBinding : registry.getTypeBindings()) {
            Debug.log("constructing: " + typeBinding);
            graph.addNode(typeBinding, context);
        }
        return context;
    }

    public Node addNode(TypeBinding<?> typeBinding, Context context) throws InjectException {
        Node node = find(typeBinding.getType(), typeBinding.getQualifier());
        if (node != null) {
            return node;
        }

        Meta meta = metaBuilder.build(typeBinding, context);
        rootNodes.add(node = new Node(meta));
        return node;
    }

    public <T> Node find(Type<T> type, Qualifier named) {
        if (type.getParameterized() != null) {
            return find(type.getParameterized().getRawType(), named, rootNodes);
        }
        return find(type.getTheClass(), named, rootNodes);
    }

    public <T> Node find(Class<T> tClass, Qualifier named) {
        return find(tClass, named, rootNodes);
    }

    private  <T> Node find(Class<T> tClass, Qualifier qualifier, Set<Node> nodes) {
        for (Node node : nodes) {
            Meta meta = node.getMeta();
            if (qualifier != null && !qualifier.equals(meta.getQualifier())) {
                continue;
            }

            Meta.Class classMeta = meta.getClazz();

            Class<?> bindClass = classMeta.getBindClass();
            if (tClass.equals(bindClass)) {
                return node;
            }

            Node foundNode = find(tClass, qualifier, node.getDescendants());
            if (foundNode != null) {
                return foundNode;
            }
        }
//        new Exception().printStackTrace();
        Debug.log(tClass + " : " + nodes);
        return null;
    }
}
