package com.sbnarra.inject.graph;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.builder.BuilderException;
import com.sbnarra.inject.meta.builder.MetaBuilder;
import com.sbnarra.inject.registry.TypeBinding;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@ToString
public class Graph {

    private final Set<Node> rootNodes = new HashSet<>();
    private final MetaBuilder metaBuilder;

    public Node<?> addNode(TypeBinding<?> typeBinding, Context context) throws GraphException {
        Node<?> node = find(typeBinding.getType(), typeBinding.getQualifier());
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

    public <T> Node<?> find(Type<T> type, Annotation annotation) {
        return find(type.getTheClass(), annotation, rootNodes);
    }

    private Node<?> find(java.lang.reflect.Type tClass, Annotation qualifier, Set<Node> nodes) {
        for (Node<?> node : nodes) {
            Meta meta = node.getMeta();
            Meta.Class<?> clazz = meta.getClazz();
            Meta.Inject inject = clazz.getInject();
            Annotation injectQualifier = inject.getQualifier();

            if (!qualiferMatches(qualifier, injectQualifier)) {
                continue;
            }

            if (meta.getInstance() != null) {
                if (!qualiferMatches(qualifier, injectQualifier)) {
                    continue;
                } else if (tClass.equals(meta.getInstance().getClass())) {
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

    private boolean qualiferMatches(Annotation qualifier, Annotation injectQualifier) {
        Debug.log(qualifier + " - " + injectQualifier);
        if (qualifier != null) {
            boolean r=  injectQualifier != null  && qualifier.equals(injectQualifier);
        Debug.log(r);
        return r;
        }
        return true;
    }
}
