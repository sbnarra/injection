package com.sbnarra.inject;

import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.registry.Registration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestIt {

    public static void main(String[] args) throws InjectException {
        Injector injector = InjectorFactory.create(new MyRegistration());
        GenericObject<String> r = Objects.requireNonNull(injector.get(new Type<GenericObject<String>>(){}), "injector returned null");
        r.message("HERE");
        r = injector.get(new Type<GenericObject<String>>(){});
        r.message("HERE");
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAn {}

    public static class MyRegistration extends Registration {
        @Override
        public void register() throws InjectException {
            bind(new Type<List<String>>(){}).with(new Type<ArrayList<String>>(){});
            bind(new Type<GenericObject<String>>(){}).with(ConcreteGeneric.class).asSingleton();

//            scoped(new Scoped(Arrays.asList((Class<Annotation>) MyAn.class)) {}).with(new ScopeHandler() {
//                @Override
//                public <T> T get(Meta<T> meta, Context context) throws InjectException {
//                    return null;
//                }
//            });

            intercept(MyAn.class).with((p, m, i, a)->i.invoke(new Object[]{"theds"}));
        }
    }
}
