package com.sbnarra.inject.aspect;

import java.lang.reflect.Method;

public interface Aspect {
    Object intercept(Object proxy, Method method, Invoker invoker, Object[] args);
}
