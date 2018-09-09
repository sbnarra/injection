package io.github.sbnarra.injection;

import io.github.sbnarra.injection.core.Type;

import java.lang.reflect.Modifier;

public class Helper {

    public static void checkBuildability(Type<?> theType) throws HelperException {
        Class<?> theClass = theType.getTheClass();
        if (theClass.isInterface()) {
            throw new HelperException(theType + ": can't build an interface");
        } else if (Modifier.isAbstract(theClass.getModifiers())) {
            throw new HelperException(theType + ": can't build an abstract class");
        }
    }

    public static class HelperException extends Exception {
        private HelperException(String msg) {
            super(msg);
        }
    }
}
