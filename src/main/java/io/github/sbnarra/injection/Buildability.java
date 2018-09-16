package io.github.sbnarra.injection;

import io.github.sbnarra.injection.core.Type;

import java.lang.reflect.Modifier;

public class Buildability {

    public static void check(Type<?> theType) throws Exception {
        Class<?> theClass = theType.getTheClass();
        if (theClass.isInterface()) {
            throw new Exception(theType + ": can't build an interface");
        } else if (Modifier.isAbstract(theClass.getModifiers())) {
            throw new Exception(theType + ": can't build an abstract class");
        }
    }

    public static class Exception extends java.lang.Exception {
        private Exception(String msg) {
            super(msg);
        }
    }
}
