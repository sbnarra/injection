package com.sbnarra.inject;

import java.util.Arrays;

public class L {

    public static void log(Object... objects) {
        System.out.println(prefix() + (objects.length == 1 ? objects[0] : Arrays.toString(objects)));
    }

    private static String prefix() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
        return ste.getClassName() + "#" + ste.getMethodName() + "(" + ste.getLineNumber() + "): ";
    }
}
