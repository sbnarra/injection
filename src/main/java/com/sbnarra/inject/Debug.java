package com.sbnarra.inject;

import java.util.Arrays;

public class Debug {
    public static void log(Object... o) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];

        Object out = o == null ? "_NULL_" : o.length == 1 ? o[0] : o.length > 1 ? Arrays.toString(o) : "_EMPTY_";
        System.out.println(stackTraceElement + ": " + out);
    }

    public static void main(String[] args) {
        Debug.log("kls");
    }
}
