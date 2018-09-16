package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@RequiredArgsConstructor
class MethodBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    void build(Class<?> theClass, Context context, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods,
                            List<Meta.Method> methodMetas, Set<Class<?>> staticsMembers) throws BuilderException {
        try {
            gatherMethods(theClass, publicProtectedMethods, defaultMethods).stream().forEach(buildMeta(theClass, context, methodMetas, staticsMembers));
        } catch (BuilderException.Unchecked e) {
            throw e.checked(BuilderException.class);
        }
    }

    private Consumer<Method> buildMeta(Class<?> theClass, Context context, List<Meta.Method> methodMetas, Set<Class<?>> staticsMembers) {
        return method -> {
            List<Meta.Parameter> parameters;
            try {
                parameters = parametersMetaBuilder.buildParameters(method, context, staticsMembers);
            } catch (BuilderException e) {
                throw e.unchecked();
            }

            if (Modifier.isStatic(method.getModifiers())) {
                staticsMembers.add(theClass);
            } else {
                Meta.Method methodMeta = Meta.Method.builder()
                        .method(method)
                        .parameters(parameters)
                        .build();
                methodMetas.add(methodMeta);
            }
        };
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        return gatherMethods(theClass, new ArrayList<>(), publicProtectedMethods, defaultMethods);
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        Package classPackage = theClass.getPackage();
        Arrays.stream(theClass.getDeclaredMethods()).forEach(gatherMethod(classPackage, injectMethods, publicProtectedMethods, defaultMethods));
        return injectMethods;
    }

    private Consumer<Method> gatherMethod(Package classPackage, List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) {
        return method -> {
            int modifier = method.getModifiers();
            if (Modifier.isStatic(modifier)) {
                addInjectMethod(method, injectMethods);
            } else {
                try {
                    gatherNonStatic(classPackage, method, modifier, injectMethods, publicProtectedMethods, defaultMethods);
                } catch (BuilderException e) {
                    e.unchecked();
                }
            }
        };
    }

    private void gatherNonStatic(Package classPackage, Method method, int modifier,
                                 List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) throws BuilderException {
        if (Modifier.isPublic(modifier) || Modifier.isProtected(modifier)) {
            if (publicProtectedMethods.stream().noneMatch(publicProtectedMethod -> Members.methodsEqual(method, publicProtectedMethod))) {
                addInjectMethod(method, injectMethods);
                publicProtectedMethods.add(method);
            }
        } else if (Modifier.isPrivate(modifier)) {
            addInjectMethod(method, injectMethods);
        } else if (modifier == 0/*isDefault*/) { // todo - final?
            List<Method> packageDefaultMethods = defaultMethods.get(classPackage);
            if (packageDefaultMethods == null) {
                defaultMethods.put(classPackage, packageDefaultMethods = new ArrayList<>());
            }

            if (packageDefaultMethods.stream().noneMatch(packageDefaultMethod -> Members.methodsEqual(method, packageDefaultMethod))) {
                addInjectMethod(method, injectMethods);
            }
            packageDefaultMethods.add(method);
        } else {
            throw new BuilderException("unknown modifier: " + Modifier.toString(modifier));
        }
    }

    private void addInjectMethod(Method method, List<Method> injectMethods) {
        if (Annotations.shouldInject(method)) {
            method.setAccessible(true);
            injectMethods.add(method);
        }
    }

}
