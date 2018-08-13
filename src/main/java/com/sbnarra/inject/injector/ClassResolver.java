package com.sbnarra.inject.injector;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.registry.Type;
import lombok.Value;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

public class ClassResolver {

    @Value
    public class Resolved {
        private final Class<?> theClass;
        private final Class<?> builderClass;
    }

    private final ByteBuddy byteBuddy;

    public ClassResolver() {
        this(new ByteBuddy().with(AnnotationRetention.ENABLED));
    }

    public ClassResolver(ByteBuddy byteBuddy) {
        this.byteBuddy = byteBuddy;
    }

    public <T> Resolved resolve(Type<T> type) throws InjectException {
        if (type.getClazz() != null) {
            return new Resolved(type.getClazz().getTheClass(), null);
        } else if (type.getParameterized() != null) {
            return resolveType(type.getParameterized().getType());
        }
        throw new InjectException("");
    }

    private Resolved resolveType(ParameterizedType parameterizedType) {
        DynamicType.Builder<?> builder = byteBuddy.subclass(parameterizedType);
        Class<?> builderClass = builder.make().load(getClass().getClassLoader()).getLoaded();
        return new Resolved(builderClass.getSuperclass(), builderClass);
    }

    public static void main(String[] args) throws InjectException {
        Type<?> type = new Type<Type<String>>() {};
        Resolved resolved = new ClassResolver().resolve(type);
        System.out.println(new ClassResolver().resolve(type));

        Constructor<?>[] constructors = resolved.getTheClass().getDeclaredConstructors();
        int i = constructors.length;
        TOP: for (Constructor<?> constructor : constructors) {
            i--;
            for (Annotation annotation : constructor.getAnnotations()) {
                if (Type.MyA.class.isInstance(annotation)) {
                    break TOP;
                }
            }
        }

        System.out.println("ANNOTATED CLASS:" + resolved.getBuilderClass().getDeclaredConstructors()[i]);

    }
}
