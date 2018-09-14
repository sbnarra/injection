package io.github.sbnarra.injection.graph;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.meta.Meta;
import io.github.sbnarra.injection.meta.builder.BuilderException;
import io.github.sbnarra.injection.meta.builder.MetaBuilder;
import io.github.sbnarra.injection.registry.TypeBinding;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@ToString
public class Graph {

    private final Set<Node<?>> rootNodes = new HashSet<>();
    private final MetaBuilder metaBuilder;
    private final List<Meta.Field> staticFieldMetas;
    private final List<Meta.Method> staticMethodMetas;

    public Node<?> addNode(TypeBinding<?> typeBinding, Context context) throws GraphException {
        Node<?> node = find(typeBinding.getType(), typeBinding.getQualifier());
        if (node != null) {
            return node;
        }

        Meta<?> meta;
        try {
            meta = metaBuilder.build(typeBinding, context, staticFieldMetas, staticMethodMetas);
        } catch (BuilderException e) {
            throw new GraphException("error building node meta: " + typeBinding, e);
        }

        rootNodes.add(node = new Node<>(meta));
        return node;
    }

    public <T> Node<?> find(Type<T> type, Annotation annotation) throws GraphException {
        return find(type.getTheClass(), annotation, rootNodes);
    }

    private Node<?> find(java.lang.reflect.Type type, Annotation qualifier, Set<Node<?>> nodes) throws GraphException {
        for (Node<?> node : nodes) {
            Meta<?> meta = node.getMeta();
            Meta.Class<?> clazz = meta.getClazz();
            Meta.Inject inject = clazz.getInject();
            Annotation injectQualifier = inject.getQualifier();

            if (!qualiferMatches(qualifier, injectQualifier)) {
                continue;
            }

            if (meta.getInstance() != null) {
                Object instance = meta.getInstance();
                if (Class.class.isAssignableFrom(type.getClass())) {
                    Class<?> typeClass = Class.class.cast(type);
                    if (typeClass.isAssignableFrom(instance.getClass())) {
                        return node;
                    } else {
                        continue;
                    }
                } else {
                    // TODO - handle other types
                    throw new GraphException("unknown type: " + type);
                }
            }

            Meta.Class<?> classMeta = meta.getClazz();
            Class<?> bindClass = classMeta.getBindClass();
            if (type.equals(bindClass)) {
                return node;
            }

            Node<?> foundNode = find(type, qualifier, node.getDescendants());
            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;
    }

    private boolean qualiferMatches(Annotation qualifier, Annotation injectQualifier) {
        return qualifier != null ? qualifier.equals(injectQualifier) : injectQualifier == null;
    }
}
