package com.sbnarra.inject;

import com.sbnarra.inject.aspect.Aspect;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.registry.Registration;
import com.sbnarra.inject.registry.RegistryException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InjectorTest {

    @Test
    public void simpleBindingTest() throws InjectException {
        String aString = "aString";
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(List.class).with(ArrayList.class);
                bind(String.class).to(aString);
            }
        });

        Assertions.assertEquals(aString, injector.get(String.class));
        Assertions.assertEquals(ArrayList.class, injector.get(List.class).getClass());
    }

    @Test
    public void injectedMembersTest() throws InjectException {
        String strOne = "strOne", strTwo = "strTwo", strThree = "strThree";
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(String.class).named(ExampleMembers.NAME_1).to(strOne);
                bind(String.class).named(ExampleMembers.NAME_2).to(strTwo);
                bind(String.class).named(ExampleMembers.NAME_3).to(strThree);
            }
        });

        ExampleMembers exampleMembers = injector.get(ExampleMembers.class);
        Assertions.assertEquals(strOne, exampleMembers.fieldParam);
        Assertions.assertEquals(strTwo, exampleMembers.constructorParam);
        Assertions.assertEquals(strThree, exampleMembers.methodParam);
    }

    private static class ExampleMembers {
        public static final String NAME_1 = "name1", NAME_2 = "name2", NAME_3 = "name3";
        @Inject
        @Named(NAME_1)
        private String fieldParam;
        private String methodParam;
        private final String constructorParam;

        @Inject
        public ExampleMembers(@Named(NAME_2) String constructorParam) {
            this.constructorParam = constructorParam;
        }

        @Inject
        public void setMethodParam(@Named(NAME_3) String methodParam) {
            this.methodParam = methodParam;
        }
    }

    @Test
    public void providerTest() throws InjectException {
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(List.class).with(ArrayList.class);
            }
        });

        ExampleProvider exampleProvider = injector.get(ExampleProvider.class);
        Assertions.assertEquals(ArrayList.class, exampleProvider.get().getClass());
    }

    public static class ExampleProvider {
        private final Provider<List<String>> provider;

        @Inject
        public ExampleProvider(Provider<List<String>> provider) {
            this.provider = provider;
        }

        public List get() {
            return provider.get();
        }
    }

    @Test
    public void namedBindingTest() throws InjectException {
        String defaultString = "defaultString";
        String bindingName = "named";
        String namedString = "namedString";
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(String.class).to(defaultString);
                bind(String.class).named(bindingName).to(namedString);
            }
        });

        Assertions.assertEquals(namedString, injector.get(String.class, bindingName));
        Assertions.assertEquals(defaultString, injector.get(String.class));
    }

    @Test
    public void parameterizedBindingTest() throws InjectException {
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(new Type<List<String>>() {}).to(Arrays.asList(""));
            }
        });

        List<String> strList = injector.get(new Type<List<String>>() {});
        List<String> stringList = injector.get(List.class);
    }

    @Test
    public void singletonScopeTest() throws InjectException {
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(List.class).with(ArrayList.class).asSingleton();
            }
        });

        List<?> theList = injector.get(List.class);
        Assertions.assertEquals(theList, injector.get(List.class));

        Assertions.assertEquals(injector.get(SingletonExample.class), injector.get(SingletonExample.class));
    }

    @Singleton
    public static class SingletonExample {
    }

    @Test
    public void interceptedMethodTest() throws InjectException {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Aspect testAspect = (proxy, method, invoker, args) -> {
            atomicBoolean.set(true);
            return invoker.invoke(args);
        };

        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() throws RegistryException {
                intercept(Intercept.class).with(testAspect);
            }
        });

        Assertions.assertFalse(atomicBoolean.get());
        injector.get(InterceptedObject.class).methodCall();
        Assertions.assertTrue(atomicBoolean.get());
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Intercept {}
    public static class InterceptedObject {
        @Intercept
        public void methodCall() {}
    }

    @Test
    public void threadLocalScopeTest() throws InjectException, InterruptedException {
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() {
                bind(List.class).with(ArrayList.class).asThreadLocal();
            }
        });

        ThreadLocalGetter<List> threadLocalGetter1 = getThreadLocalGetter(injector, List.class);
        ThreadLocalGetter<List> threadLocalGetter2 = getThreadLocalGetter(injector, List.class);
        Assertions.assertTrue(threadLocalGetter1.getOne() != threadLocalGetter2.getOne());
    }

    private <T> ThreadLocalGetter<T> getThreadLocalGetter(Injector injector, Class<T> tClass) throws InterruptedException {
        ThreadLocalGetter<T> threadLocalGetter = new ThreadLocalGetter(injector, tClass);

        Thread thread = new Thread(threadLocalGetter);
        thread.start();
        thread.join();

        Assertions.assertNotNull(threadLocalGetter.getOne());
        Assertions.assertNotNull(threadLocalGetter.getTwo());

        Assertions.assertTrue(threadLocalGetter.getOne().hashCode() == threadLocalGetter.getTwo().hashCode());
        return threadLocalGetter;
    }

    @RequiredArgsConstructor
    @Getter
    private class ThreadLocalGetter<T> implements Runnable {
        private final Injector injector;
        private final Class<T> theClass;

        private T one;
        private T two;

        @Override
        public void run() {
            try {
                one = injector.get(theClass);
                two = injector.get(theClass);
            } catch (InjectException e) {
                throw e.unchecked();
            }
        }
    }
}
