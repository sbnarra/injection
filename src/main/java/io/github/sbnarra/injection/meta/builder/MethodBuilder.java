package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class MethodBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Method> build(Class<?> theClass, Context context, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        return gatherMethods(theClass, publicProtectedMethods, defaultMethods).stream().map(method -> {
            List<Meta.Parameter> parameters = null;
            try {
                parameters = parametersMetaBuilder.buildParameters(method, context);
            } catch (BuilderException e) {
                e.unchecked();
            }

            return Meta.Method.builder()
                    .method(method)
                    .parameters(parameters)
                    .build();
        }).collect(Collectors.toList());
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        return gatherMethods(theClass, new ArrayList<>(), publicProtectedMethods, defaultMethods);
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        Package classPackage = theClass.getPackage();
        Stream.of(theClass.getDeclaredMethods()).forEach(gatherMethod(classPackage, injectMethods, publicProtectedMethods, defaultMethods));
        return injectMethods;
    }

    private Consumer<Method> gatherMethod(Package classPackage, List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) {
        return method -> {
            int modifier = method.getModifiers();
            boolean isPublic = Modifier.isPublic(modifier);
            if (Modifier.isStatic(modifier)) {
                addInjectMethod(method, isPublic, injectMethods);
            } else {
                try {
                    gatherNonStatic(classPackage, method, isPublic, modifier, injectMethods, publicProtectedMethods, defaultMethods);
                } catch (BuilderException e) {
                    e.unchecked();
                }
            }
        };
    }

    private void gatherNonStatic(Package classPackage, Method method, boolean isPublic, int modifier,
                                 List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        if (isPublic || Modifier.isProtected(modifier)) {
            if (publicProtectedMethods.stream().noneMatch(publicProtectedMethod -> methodsEqual(method, publicProtectedMethod))) {
                addInjectMethod(method, isPublic, injectMethods);
                publicProtectedMethods.add(method);
            }
        } else if (Modifier.isPrivate(modifier)) {
            addInjectMethod(method, false, injectMethods);
        } else if (modifier == 0/*isDefault*/) {
            List<Method> packageDefaultMethods = defaultMethods.get(classPackage);
            if (packageDefaultMethods == null) {
                defaultMethods.put(classPackage, packageDefaultMethods = new ArrayList<>());
            }

            if (packageDefaultMethods.stream().noneMatch(packageDefaultMethod -> methodsEqual(method, packageDefaultMethod))) {
                addInjectMethod(method, false, injectMethods);
            }
            packageDefaultMethods.add(method);
        } else {
            throw new BuilderException("unknown modifier: " + Modifier.toString(modifier));
        }
    }

    private void addInjectMethod(Method method, boolean isPublic, List<Method> injectMethods) {
        if (Annotations.shouldInject(method)) {
            method.setAccessible(true);
            injectMethods.add(method);
        }
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
