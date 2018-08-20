package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.TypeBinding;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class ParametersMetaBuilder {
    private final Annotations annotations;

    List<Meta> getParameters(Executable executable, Graph graph, Registry registry) throws InjectException {
        List<Meta> metas = new ArrayList<>();
        Annotation[][] annotations = executable.getParameterAnnotations();
        Class<?>[] types = executable.getParameterTypes();
        for (int i = 0; i < executable.getParameterCount(); i++) {
            Class<?> type = types[i];
            String named = this.annotations.getName(annotations[i]);
            metas.add(getParameter(type, new Qualifier.Named(named), graph, registry));
        }
        return metas;
    }

    Meta getParameter(Class<?> paramClass, Qualifier qualifier, Graph graph, Registry registry) throws InjectException {
        Graph.Node node = graph.find(paramClass, qualifier);
        if (node == null) {
            TypeBinding<?> typeBinding = registry.find(paramClass);
            node = graph.addNode(typeBinding, registry);
        }
        return node.getMeta();
    }
}
