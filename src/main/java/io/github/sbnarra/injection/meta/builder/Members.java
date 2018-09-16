package io.github.sbnarra.injection.meta.builder;

import java.lang.reflect.Method;

class Members {

    static boolean methodsEqual(Method m1, Method m2) {
        return m1.getName().equals(m2.getName()) &&
                m1.getReturnType().isAssignableFrom(m2.getReturnType()) &&
                parametersEqual(m1.getParameterTypes(), m2.getParameterTypes());
    }

    private static boolean parametersEqual(Class<?>[] params1, Class<?>[] params2) {
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
