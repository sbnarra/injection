package com.sbnarra.inject;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class DeleteMe {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        new ByteBuddy()
                .subclass(Example.class)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        L.log("invoke intercepted method");

                        return proxy.getClass()
                                .getSuperclass()
                                .getMethod(method.getName(), method.getParameterTypes())
                                .invoke(proxy, args);
                        //return method.invoke(proxy, args);
                    }
                }))
                .make()
                .load(Example.class.getClassLoader())
                .getLoaded()
                .newInstance().myMethod();
    }

//    public static class Example {
//
//        public void myMethod() {
//            System.out.println("Example.myMethod");
//        }
//    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Ann {}
}
