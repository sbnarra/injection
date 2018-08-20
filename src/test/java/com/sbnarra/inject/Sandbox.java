package com.sbnarra.inject;

import com.sbnarra.inject.aspect.Aspect;
import com.sbnarra.inject.aspect.AspectInvoker;
import com.sbnarra.inject.aspect.Invoker;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public class Sandbox {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        testIt(StaticClass.class).message("hello");
        testIt(AbstractStaticClass.class).message("hello");
        testIt(Interface.class).message("hello");
        testIt(ConcreteGeneric.class).message("hello");
    }

    public static <T> T testIt(Class<T> aClass) throws IllegalAccessException, InstantiationException {
        return new ByteBuddy().with(AnnotationRetention.ENABLED)
                .subclass(aClass)
                .method(
                        ElementMatchers.isAnnotatedWith(TestIt.MyAn.class)
                ).intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .withBinders(Morph.Binder.install(Invoker.class))
                                .to(new AspectInvoker((p, m, i, a)->i.invoke(new Object[]{"there1"}))))

                .make()
                .load(Example.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
                .newInstance();
    }

//    public interface Invoker {
//        Object invoke(Object[] args);
//    }

//    @RequiredArgsConstructor
//    public static class Interceptor {
//        private final Aspect aspect;
//        @RuntimeType
//        public Object intercept(@This Object proxy, @Origin Method method, @Morph Invoker invoker, @AllArguments Object[] args) {
//            return aspect.intercept(proxy, method, invoker, new Object[]{"there"});
//        }
//    }

    public static class StaticClass {
        @TestIt.MyAn
        public String message(String message) {
            System.out.println(getClass().getName() + ": message: " + message);
            return message;
        }
    }

    public static abstract class AbstractStaticClass {
        @TestIt.MyAn
        public String message(String message) {
            System.out.println(getClass().getName() + ": message: " + message);
            return message;
        }
    }

    public interface Interface {
        @TestIt.MyAn
        default String message(String message) {
            System.out.println(getClass().getName() + ": message: " + message);
            return message;
        }
    }
//
//    @Retention(RetentionPolicy.RUNTIME)
//    @Target(ElementType.METHOD)
//    public @interface AnAnnotation {}
}
