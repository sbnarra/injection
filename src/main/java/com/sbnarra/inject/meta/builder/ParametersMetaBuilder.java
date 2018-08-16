package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.graph.DependencyNode;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Registry;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

class ParametersMetaBuilder {

    List<ObjectMeta> getParameters(Executable executable, Graph graph, Registry registry) throws InjectException {
        List<ObjectMeta> objectMetas = new ArrayList<>();
        for (Class<?> paramClass : executable.getParameterTypes()) {
            objectMetas.add(getParameter(paramClass, graph, registry));
        }
        return objectMetas;
    }

    ObjectMeta getParameter(Class<?> paramClass, Graph graph, Registry registry) throws InjectException {
        DependencyNode dependencyNode = graph.find(paramClass);
        if (dependencyNode == null) {
            Binding<?> binding = registry.find(paramClass);
            dependencyNode = graph.addNode(binding, registry);
        }
        return dependencyNode.getObjectMeta();
    }
}
