package com.sbnarra.inject.injector;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ConstructorMeta;
import com.sbnarra.inject.meta.ObjectMeta;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RequiredArgsConstructor
public class ObjectCreator {

    public <T> T create(@NonNull ObjectMeta objectMeta) throws InjectException {
        ConstructorMeta constructorMeta = objectMeta.getConstructorMeta();
        Constructor<T> constructor = constructorMeta.getConstructor();

        try {
            return constructor.newInstance(getParameters(constructorMeta.getFields()));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new InjectException("error building new instance", e);
        }
    }

    private Object[] getParameters(List<ObjectMeta> argMetas) throws InjectException {
        Object[] args = new Object[argMetas.size()];
        for (int i = 0; i < argMetas.size(); i++) {
            args[i] = create(argMetas.get(i));
        }
        return args;
    }
}
