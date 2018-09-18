package io.github.sbnarra.injection.aspect;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class AspectInvoker {
    private final Aspect aspect;

    @RuntimeType
    public Object intercept(@This Object proxy, @Origin Method method, @Morph Invoker invoker, @AllArguments Object[] args) {
        return aspect.intercept(proxy, method, invoker, args);
    }
}
