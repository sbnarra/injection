package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class MethodBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Method> build(Meta.Class<?> classMeta, Context context) throws BuilderException {
        return build(classMeta.getContractClass(), context);
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> injectMethods, List<Method> dontInjectMethods) {
        for (Method method : theClass.getDeclaredMethods()) {
            if (!Modifier.isPrivate(method.getModifiers())) {
                if (dontInjectMethods.stream().noneMatch(overriddenWithoutInject -> methodsEqual(method, overriddenWithoutInject))) {
                    if (Annotations.shouldInject(method)) {
                        injectMethods.add(method);
                    } else {
                        dontInjectMethods.add(method);
                    }
                }
            } else if (Annotations.shouldInject(method)) {
                injectMethods.add(method);
            }
        }

        if (!Object.class.equals(theClass.getSuperclass())) {
            return gatherMethods(theClass.getSuperclass(), injectMethods, dontInjectMethods);
        }

        return injectMethods;
    }

    private List<Meta.Method> build(Class<?> theClass, Context context) throws BuilderException {
        List<Meta.Method> injectMethods = new ArrayList<>();
        List<Method> methods = gatherMethods(theClass, new ArrayList<>(), new ArrayList<>());
        for (Method method : methods) {
            method.setAccessible(true);
            injectMethods.add(Meta.Method.builder()
                    .method(method)
                    .parameters(parametersMetaBuilder.getParameters(method, context))
                    .build());
        }

        return injectMethods;
    }

    private boolean methodsEqual(Method m1, Method m2) {
        return m1.getName().equals(m2.getName()) &&
                m1.getReturnType().isAssignableFrom(m2.getReturnType()) &&
                parametersEqual(m1.getParameterTypes(), m2.getParameterTypes());
    }

    private boolean parametersEqual(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }
}
