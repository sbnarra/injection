package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.InjectException;
import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.core.Debug;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class MethodBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Method> build(Meta.Class<?> classMeta, Context context) throws BuilderException {
        List<Meta.Method> methods = new ArrayList<>();
        List<Method> seenMethods = new ArrayList<>();
        return build(classMeta.getContractClass(), context, methods, seenMethods);
    }

    private List<Meta.Method> build(Class<?> theClass, Context context, List<Meta.Method> injectMethods, List<Method> seenMethods) throws BuilderException {
        for (Method method : Annotations.findInject(theClass.getDeclaredMethods())) {
            if (seenMethods.stream().anyMatch(m -> methodsEqual(method, m))) {
                // overridden from child class
                continue;
            }
            seenMethods.add(method);

            method.setAccessible(true);
            injectMethods.add(Meta.Method.builder()
                    .method(method)
                    .parameters(parametersMetaBuilder.getParameters(method, context))
                    .build());
        }

        if (theClass.getSuperclass() != null) {
            return build(theClass.getSuperclass(), context, injectMethods, seenMethods);
        }
        return injectMethods;
    }

    boolean methodsEqual(Method m1, Method m2) {
        if (m1.getName() == m2.getName() && m1.getReturnType().equals(m2.getReturnType())) {
            return parametersEqual(m1.getParameterTypes(), m2.getParameterTypes());
        }
        return false;
    }

    boolean parametersEqual(Class<?>[] params1, Class<?>[] params2) {
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
