package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.InjectionAnnotations;
import com.sbnarra.inject.graph.DependencyNode;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Registry;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class ParametersMetaBuilder {
    private final InjectionAnnotations injectionAnnotations;

    List<ObjectMeta> getParameters(Executable executable, Graph graph, Registry registry) throws InjectException {
        List<ObjectMeta> objectMetas = new ArrayList<>();

        Annotation[][] annotations = executable.getParameterAnnotations();
        Class<?>[] types = executable.getParameterTypes();
        for (int i = 0; i < executable.getParameterCount(); i++) {

            Class<?> type = types[i];
            String named = injectionAnnotations.getName(annotations[i]);
            objectMetas.add(getParameter(type, named, graph, registry));
        }

        for (Class<?> paramClass : executable.getParameterTypes()) {


        }
        return objectMetas;
    }

    ObjectMeta getParameter(Class<?> paramClass, String named, Graph graph, Registry registry) throws InjectException {
        DependencyNode dependencyNode = graph.find(paramClass, named);
        if (dependencyNode == null) {
            Binding<?> binding = registry.find(paramClass);
            dependencyNode = graph.addNode(binding, registry);
        }
        return dependencyNode.getObjectMeta();
    }
}
