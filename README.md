[![Build Status](https://travis-ci.org/sbnarra/injection.svg?branch=master)](https://travis-ci.org/sbnarra/injection) [![Coverage Status](https://coveralls.io/repos/github/sbnarra/injection/badge.svg?branch=master)](https://coveralls.io/github/sbnarra/injection?branch=master) master

# An Injection Framework

Quite simply, this is an implementation of the `javax.inject` classes as per the [JSR-330](http://javax-inject.github.io/javax-inject/) spec.

What does this mean? In short you don't call `new` to create objects, we do that for you.

## Getting Started

### Installing the Dependency

This project isn't yet available in the maven central repository and needs to be installed locally to use in your own projects.
```bash
git clone https://github.com/sbnarra/injection.git \ # get a copy
    && cd injection && mvn install \ # install the copy
    && cd .. && rm -r injection # delete the copy (without deleting installed version)
```

### Including the Dependency

Add the following to the dependencies section of your projects `pom.xml`.
```xml
<dependency>
    <groupId>io.github.sbnarra.injection</groupId>
    <artifactId>injection</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Code Example

As long as you're only dealing with single constructor concrete types you can start using the injector without any configuration...
```java
import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.InjectorFactory;
...
Injector injector = InjectorFactory.create();
MyConreteObject injected = injector.getInstance(MyConreteObject.class);
```

Otherwise you can register bindings by providing [Registrations](#Registration) to the `InjectorFactory`...

```java
import io.github.sbnarra.injection.Injector;
import io.github.sbnarra.injection.InjectorFactory;

import io.github.sbnarra.injection.registry.Registration;
import io.github.sbnarra.injection.registry.RegistryException;
...
Injector injector = InjectorFactory.create(new Registration() {
    @Override
    public void register() throws RegistryException {
        // to allow the injector to create your injected instance use
        bind(ExampleInterface.class).with(ExampleClass.class);
        // or to provide an instance to be injected use
        bind(ExampleInterface.class).to(new ExampleClass());
    }
});
ExampleClass exampleClass = injector.getInstance(ExampleInterface.class);
```

...and annotating your classes using the JSR-330 annotations...
* `javax.inject.Inject` - add to your constructor, field or method definitions allowing the injector to provide dependencies
* `javax.inject.Singleton`, an scope implementation - add to your class definitions telling the injector to only create a single instance
* `javax.inject.Named`, an qualifier implementation - add to your field or parameter definitions telling the injector to provide specific qualified version of an instance

...or by defining your own scope or qualifier annotations using...
* `javax.inject.Scope` - see the scopes section below for more information.
* `javax.inject.Qualifier` -  see the qualifiers section below for more information.

## Registration

Registration classes provide a means of registering contract bindings. The above example shows a simple binding contract with both a class and instance. These can be extended to provide more complex contracts using [scopes](#Scopes), [qualifiers](#Qualifiers) and [aspects](#Aspects).

Registration classes also provides the ability to register custom scope annotations and scope handlers along with aspect annotations and aspect handlers.

### [Scopes](docs/scopes.md)

Scopes tell the injector when to construct a new instance of an object or when to reuse a previously constructed version. For example annotating your classes with the `javax.inject.Singleton` annotation will result in the injector only creating one instance of that object and providing it whenever it's requested. On the other hand providing no scoping annotation simply means the injector will create a new instance every time the object is created.

For more information on scopes, have a read of the [scoping documentation](docs/scopes.md).

### [Qualifiers](docs/qualifiers.md)

Qualifiers tell the injector what type of contract a binding should use. For example if you have 2 concrete implementations of an interface or abstract class, you can annotate field and parameter declarations using the `javax.inject.Named("implementationType")` annotation, then register the binding with the same qualifier to the implementation type.

The below demonstrates the use of the named qualifier to inject two different instances of the same type.
```java
class DefaultBooleanSupplier implements BooleanSupplier {
    public boolean getAsBoolean() {
        return true;
    }
}

class NamedBooleanSupplier implements BooleanSupplier {
    public boolean getAsBoolean() {
        return true;
    }
}

class QualifiedExample {
    private final BooleanSupplier defaultBooleanSupplier, namedBooleanSupplier;
    @Inject
    QualifiedExample(BooleanSupplier defaultBooleanSupplier, @Named("named") BooleanSupplier namedBooleanSupplier) {
        this.defaultBooleanSupplier = defaultBooleanSupplier;
        this.namedBooleanSupplier = namedBooleanSupplier;
    }

    public static void main(String[] args){
        Injector injector = InjectorFactory.create(new Registration() {
            @Override
            public void register() throws RegistryException {
                bind(BooleanSupplier.class).with(DefaultBooleanSupplier.class);
                bind(BooleanSupplier.class).named("named").with(NamedBooleanSupplier.class);
            }
        });
        QualifiedExample qualifiedExample = injector.getInstance(QualifiedExample.class);
        assert qualifiedExample.defaultBooleanSupplier == true;
        assert qualifiedExample.namedBooleanSupplier == false;
        
        assert injector.getInstance(BooleanSupplier.class).getAsBoolean() == true;
        assert injector.getInstance(BooleanSupplier.class, "named").getAsBoolean() == false;
    }
}
```

For more information on qualifiers, have a read of the [qualifiers documentation](docs/qualifiers.md).

### [Aspects](docs/aspects.md)

Aspects can be defined with custom annotations on methods, these annotations are then registered with `Aspect` implementations.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OutputDuration {}

class AspectExample {
    @OutputDuration
    public void sleep(long duration) {
        try {
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
      Injector injector = InjectorFactory.create(new Registration() {
          @Override
          public void register() throws RegistryException {
              intercept(OutputDuration.class)
              .with((proxy, method, invoker, args) -> {
                  long started = System.currentTimeMillis();
                  Object ret = invoker.invoke(args);
                  System.out.println("took " + (System.currentTimeMillis() - started) + "ms");
                  return ret;
              });
          }
      });
      
      AspectExample aspectExample = injector.getInstance(AspectExample.class);
      aspectExample.sleep(50L);
      aspectExample.sleep(20L);
    }
}
```

For more information on aspects, have a read of the [aspects documentation](docs/aspects.md).

## Built With

* [lombok](https://github.com/rzwitserloot/lombok) - Static Code Generation
* [Byte Buddy](https://github.com/raphw/byte-buddy) - Runtime Code Generation

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/sbnarra/injection/tags). 

<!---
## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
-->
