package io.github.sbnarra.injection.context;

import io.github.sbnarra.injection.InjectException;
import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.core.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class StaticContext {

    private final static Set<Class<?>> injectedStaticClasses = new HashSet<>();

    public static void inject(Set<Class<?>> allStaticClasses, Injector injector) throws ContextException {
        Set<Class<?>> newInjectedStaticClasses = cleanSupertypes(allStaticClasses);
        try {
            Set<Class<?>> injectedClasses = new HashSet<>();
            newInjectedStaticClasses.stream().forEach(theClass -> {
                synchronized (theClass) {
                    try {
                        injectStatics(theClass, injector, injectedClasses);
                        injectedStaticClasses.add(theClass);
                    } catch (ContextException e) {
                        throw e.unchecked();
                    }
                }
            });
        } catch (ContextException.Unchecked e) {
            throw e.checked(ContextException.class);
        }
    }

    private static Set<Class<?>> cleanSupertypes(Set<Class<?>> allStaticClasses) {
        Set<Class<?>> newInjectedStaticClasses = new HashSet<>();
        synchronized (newInjectedStaticClasses) {
            for (Class<?> allStaticClass : allStaticClasses) {
                boolean dontAdd = false;
                Iterator<Class<?>> staticClassesIterator = newInjectedStaticClasses.iterator();
                while (staticClassesIterator.hasNext()) {
                    Class<?> staticClass = staticClassesIterator.next();
                    if (staticClass.isAssignableFrom(allStaticClass)) {
                        staticClassesIterator.remove();
                    } else if (allStaticClass.isAssignableFrom(staticClass)) {
                        dontAdd = true;
                    }
                }

                if (!dontAdd) {
                    newInjectedStaticClasses.add(allStaticClass);
                }
            }
        }
        newInjectedStaticClasses.removeAll(injectedStaticClasses);
        return newInjectedStaticClasses;
    }

    private static void injectStatics(Class<?> theClass, Injector injector, Set<Class<?>> injectedClasses) throws ContextException {
        if (!Object.class.equals(theClass.getSuperclass())) {
            injectStatics(theClass.getSuperclass(), injector, injectedClasses);
        }

        if (injectedClasses.contains(theClass)) {
            return;
        }

        injectStaticFields(theClass, injector);
        injectStaticsMethods(theClass, injector);
        injectedClasses.add(theClass);
    }

    private static void injectStaticFields(Class<?> theClass, Injector injector) throws ContextException {
        for (Field field : theClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && Annotations.shouldInject(field)) {
                field.setAccessible(true);
                Annotation qualifier = Annotations.findQualifier(field);
                Annotation scope = Annotations.findScope(field);

                try {
                    Type type = new Type<Object>(field.getGenericType()) {};
                    Object value = injector.get(type, qualifier, scope);
                    field.set(null, value);
                } catch (IllegalAccessException | InjectException e) {
                    throw new ContextException("static field inject error: " + field, e);
                }
            }
        }
    }

    private static void injectStaticsMethods(Class<?> theClass, Injector injector) throws ContextException {
        for (Method method : theClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && Annotations.shouldInject(method)) {
                method.setAccessible(true);

                try {
                    method.invoke(null, getParameters(method, injector));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new ContextException("static method inject error: " + method, e);
                }
            }
        }
    }

    private static Object[] getParameters(Method method, Injector injector) throws ContextException {
        Annotation[][] parametersAnnotations = method.getParameterAnnotations();
        java.lang.reflect.Type[] parameterTypes = method.getGenericParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        IntStream.range(0, parameterTypes.length).forEach(getParameter(args, method, injector, parametersAnnotations, parameterTypes));
        return args;
    }

    private static IntConsumer getParameter(Object[] args, Method method, Injector injector, Annotation[][] parametersAnnotations, java.lang.reflect.Type[] parameterTypes) throws ContextException {
        try {
        return i -> {
            Annotation[] parameterAnnotations = parametersAnnotations[i];
            Annotation qualifier = Annotations.findQualifierAnnotation(parameterAnnotations);
            Annotation scope = Annotations.findScopeAnnotation(parameterAnnotations);

            try {
                Type<?> type = new Type<Object>(parameterTypes[i]) {};
                args[i] = injector.get(type, qualifier, scope);
            } catch (InjectException e) {
                throw e.unchecked();
            }};
        } catch (InjectException.Unchecked e) {
            throw new ContextException("static method inject error: " + method + ":" + Arrays.toString(parameterTypes), e.checked(InjectException.class));
        }
    }

}
