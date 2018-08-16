package com.sbnarra.inject;

import com.sbnarra.inject.registry.Registration;
import com.sbnarra.inject.registry.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestIt {

    public static void main(String[] args) throws InjectException {
        Injector injector = InjectorFactory.create(new MyRegistration());
        GenericObject<String> r = injector.get(new Type<GenericObject<String>>(){});
        System.out.println(r);

        System.out.println(injector.get(new Type<GenericObject<List<String>>>(){}));
        System.out.println(injector.get(SimpleObject.class));
        r.doSomething();
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAn {}

    public static class MyRegistration extends Registration {
        @Override
        public void register() throws InjectException {
            bind(new Type<List<String>>(){}).with(new Type<ArrayList<String>>(){});
            bind(new Type<GenericObject<String>>(){}).with(ConcreteGeneric.class);
            intercept(MyAn.class).with(new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    L.log("intercept");
                    return method.invoke(proxy, args);
                }
            });
        }
    }
}
