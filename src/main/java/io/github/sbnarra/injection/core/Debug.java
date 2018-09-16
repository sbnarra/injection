package io.github.sbnarra.injection.core;

import java.util.Arrays;

@Deprecated
public class Debug {
    @Deprecated
    public static void log(Exception e) {

        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        System.out.println(stackTraceElement + ": " + e.getMessage());
        e.printStackTrace(System.out);
    }

    @Deprecated
    public static void log(Object... o) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];

        Object out = o == null ? "_NULL_" : o.length == 1 ? o[0] : o.length > 1 ? Arrays.toString(o) : "_EMPTY_";
        System.out.println(stackTraceElement + ": " + out);
    }
}
