package com.sbnarra.inject;

import com.sbnarra.inject.aspect.AspectInvoker;
import com.sbnarra.inject.aspect.Invoker;
import lombok.NoArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.attribute.AnnotationRetention;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static net.bytebuddy.matcher.ElementMatchers.hasAnnotation;
import static net.bytebuddy.matcher.ElementMatchers.inheritsAnnotation;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class DeleteMe {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

                new ByteBuddy().with(AnnotationRetention.ENABLED)
                .subclass(MorphTargetI.class)
                .method(
                                ElementMatchers.isAnnotatedWith(TestIt.MyAn.class)

                ).intercept(
                        MethodDelegation.withDefaultConfiguration()
                        .withBinders(Morph.Binder.install(Invoker.class))
                        .to(new AspectInvoker((p, a,b,c)->b.invoke(new Object[]{"there"}))))

                .make()
                .load(Example.class.getClassLoader())
                .getLoaded()
                .newInstance().message("hello");
    }

    public static class MorphTarget {

        @TestIt.MyAn
        String message(String message) {
            System.out.format("  Called default message(\"%s\")%n", message);
            return message;
        }
    }

    public interface MorphTargetI {

        @TestIt.MyAn
        default String message(String message) {
            System.out.format("  Called default message(\"%s\")%n", message);
            return message;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Ann {}
}
