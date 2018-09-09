# Scopes

Scopes tell the injector when to construct a new instance of an object or when to reuse a previously constructed version.
 
## Supported Scopes

`javax.inject.Singleton`

Injector creates a single instance of the requested type which is reused each time it's requested.

`io.github.sbnarra.injection.ThreadLocal`

Injector creates a single instance of the requested type per thread which is reused each time it's requested within that thread.

`io.github.sbnarra.injection.InheritedThreadLocal`

Injector creates a single instance of the requested type per thread which is reused each time it's requested within that thread or a child thread.

## Custom Scopes

Custom scopes can be created by following the below steps.

#### Create a new annotation

Your new annotation needs to be annotated with the `@Scope` annotation so the injector can identify the scope during runtime. See the below example.

```java
@Scope
@Retention(RUNTIME)
public @interface CustomScope {
}
```

#### Create a new scope handler implementation

Your new scope handler needs to implement methods to provide an instance of an requested object and to destroy that scope.

Below is a simple example of the ScopeHandler which creates a new instance with each request, printing that objects meta. Here you can cache objects for reuse or create new instances.

```java
public class CustomScopeHandler implements ScopeHandler {
    @Override
     public void destoryScope() throws ScopeHandlerException {
     }

     @Override
     public <T> T get(Meta<T> meta, Context context) throws ScopeHandlerException {
        try {
            System.out.println("creating new instance: " + meta);
            return context.get(meta);
        } catch (ContextException e) {
            throw new ScopeHandlerException("failed to create new instance", e);
        }
    }
}
```

#### Bind your new annotation to an instance of your new scope handler

Create a new scope registration to bind your scope annotation with the handler which can be passed to your injector.

```java
public class MyScopeRegistration extends Registration {
    @Override
    public void register() throws RegistryException {
        scoped(CustomScope.class).with(new CustomScopeHandler());
    }
}
```
 
#### Decorate your dependencies with your new annotation

If you've followed the steps with the exact same code as above you should now see the message `creating new instance: <meta>` printed once you request an instance of your custom scoped object.

```java
@CustomScope
public class InjectedWithCustomScope {
    public static void main(String[] args){
      Injector injector = InjectorFactory.create(new MyScopeRegistration());
      InjectedWithCustomScope injectedWithCustomScope = injector.get(InjectedWithCustomScope.class);
    }
}
```