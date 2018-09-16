package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.annotation.Annotations;
import io.github.sbnarra.injection.context.Context;
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
import java.util.function.Predicate;

@RequiredArgsConstructor
class MethodBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;
    private final Annotations annotations;

    void build(Class<?> theClass, Context context, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods,
                            List<Meta.Method> methodMetas, Set<Class<?>> staticsMembers) throws BuilderException {
        try {
            gatherMethods(theClass, publicProtectedMethods, defaultMethods).forEach(buildMeta(theClass, context, methodMetas, staticsMembers));
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
                return;
            }

            Meta.Method methodMeta = Meta.Method.builder()
                    .method(method)
                    .parameters(parameters)
                    .build();
            methodMetas.add(methodMeta);
        };
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) {
        return gatherMethods(theClass, new ArrayList<>(), publicProtectedMethods, defaultMethods);
    }

    private List<Method> gatherMethods(Class<?> theClass, List<Method> injectMethods, List<Method> publicProtectedMethods, Map<Package, List<Method>> defaultMethods) {
        Consumer<Method> gatherMethod = gatherMethod(theClass.getPackage(), injectMethods, publicProtectedMethods, defaultMethods);
        Arrays.stream(theClass.getDeclaredMethods()).forEach(gatherMethod);
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
            Predicate<Method> methodEquals = methodEquals(method);
            if (publicProtectedMethods.stream().noneMatch(methodEquals)) {
                addInjectMethod(method, injectMethods);
                publicProtectedMethods.add(method);
            }
        } else if (Modifier.isPrivate(modifier)) {
            addInjectMethod(method, injectMethods);
        } else if (modifier == 0/*isDefault*/) { // todo - final?
            gatherDefaultNonStatic(classPackage, method, injectMethods, defaultMethods);
        } else {
            throw new BuilderException("unknown modifier: " + Modifier.toString(modifier));
        }
    }

    private void gatherDefaultNonStatic(Package classPackage, Method method, List<Method> injectMethods, Map<Package, List<Method>> defaultMethods) {
        List<Method> packageDefaultMethods = defaultMethods.computeIfAbsent(classPackage, k -> new ArrayList<>());
        Predicate<Method> methodEquals = methodEquals(method);
        if (packageDefaultMethods.stream().noneMatch(methodEquals)) {
            addInjectMethod(method, injectMethods);
        }
        packageDefaultMethods.add(method);
    }

    private static Predicate<Method> methodEquals(Method method) {
        return packageDefaultMethod -> Members.methodsEqual(method, packageDefaultMethod);
    }

    private void addInjectMethod(Method method, List<Method> injectMethods) {
        if (annotations.hasInjectAnnotation(method)) {
            method.setAccessible(true);
            injectMethods.add(method);
        }
    }
}
