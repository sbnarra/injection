package com.sbnarra.inject;

import com.sbnarra.inject.graph.DependencyNode;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.ConstructorMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Binding;
import com.sbnarra.inject.registry.Registry;
import com.sbnarra.inject.registry.Type;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RequiredArgsConstructor
class DefaultInjector implements Injector {

    private final Registry registry;
    private final Graph graph;

    @Override
    public <T> T get(Class<T> tClass, String named) throws InjectException {
        DependencyNode dependencyNode = graph.find(tClass,  named);
        if (dependencyNode == null) {
            dependencyNode = graph.addNode(new Binding<>(tClass).named(named).with(tClass).getBinding(), registry);
        }
        return create(dependencyNode.getObjectMeta());
    }

    @Override
    public <T> T get(Type<T> type, String named) throws InjectException {
        DependencyNode dependencyNode = graph.find(type, named);
        if (dependencyNode == null) {
            throw new InjectException("no binding found: " + type);
        }
        T t = create(dependencyNode.getObjectMeta());
        injectMembers(t, dependencyNode);
        return t;
    }

    private <T> T create(@NonNull ObjectMeta objectMeta) throws InjectException {
        ConstructorMeta constructorMeta = objectMeta.getConstructorMeta();
        Constructor<T> constructor = constructorMeta.getConstructor();

        try {
            return constructor.newInstance(getParameters(constructorMeta.getFields()));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InjectException("error building new instance", e);
        }
    }

    private <T> void injectMembers(T t, DependencyNode dependencyNode) {

    }

    private Object[] getParameters(List<ObjectMeta> argMetas) throws InjectException {
        Object[] args = new Object[argMetas.size()];
        for (int i = 0; i < argMetas.size(); i++) {
            args[i] = create(argMetas.get(i));
        }
        return args;
    }
}
