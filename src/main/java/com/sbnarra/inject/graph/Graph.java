package com.sbnarra.inject.graph;

import com.sbnarra.inject.TypeBinding;
import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.meta.builder.BuilderException;
import com.sbnarra.inject.meta.builder.MetaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@ToString
public class Graph {

    private final Set<Node> rootNodes = new HashSet<>();
    private final MetaBuilder metaBuilder;

    public Node addNode(TypeBinding<?> typeBinding, Context context) throws GraphException {
        Node node = find(typeBinding.getType(), typeBinding.getQualifier());
        if (node != null) {
            return node;
        }

        Meta meta;
        try {
            meta = metaBuilder.build(typeBinding, context);
        } catch (BuilderException e) {
            throw new GraphException("error building node meta: " + typeBinding, e);
        }

        rootNodes.add(node = new Node(meta));
        return node;
    }

    public <T> Node find(Type<T> type, Qualifier named) {
        if (type.getParameterized() != null) {
            return find(type.getParameterized().getRawType(), named, rootNodes);
        }
        return find(type.getTheClass(), named, rootNodes);
    }

    private  <T> Node find(Class<T> tClass, Qualifier qualifier, Set<Node> nodes) {
        for (Node node : nodes) {
            Meta meta = node.getMeta();
            if (qualifier != null && !qualifier.equals(meta.getQualifier())) {
                continue;
            }

            if (meta.getInstance() != null) {
                if (tClass.equals(meta.getInstance().getClass())) {
                    return node;
                }
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
        return null;
    }
}
