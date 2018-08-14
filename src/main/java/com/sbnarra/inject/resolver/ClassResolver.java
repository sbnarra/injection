package com.sbnarra.inject.resolver;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

@RequiredArgsConstructor
public class ClassResolver {

    private final ByteBuddy byteBuddy;

    private final ConstructorResolver constructorResolver;
    private final MethodResolver methodResolver;
    private final FieldResolver fieldResolver;

    public <T> ClassMeta resolve(Type<T> type) throws InjectException {
        ClassMeta.ClassMetaBuilder builder = ClassMeta.builder();

         if (type.getParameterized() != null) {
             resolveType(type, builder);
        } else {
             builder.baseClass(type.getClazz().getTheClass());
         }

        builder.constructorMeta(constructorResolver.resolve());
        builder.fieldMeta(...);
        builder.methodMeta(...);

        return builder.build();
    }

    private void resolveType(Type<?> type, ClassMeta.ClassMetaBuilder metaBuilder) {
        Class<?> builderClass = byteBuddy
                .subclass(type.getParameterized().getType())
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        metaBuilder.buildWith(builderClass);
        metaBuilder.baseClass(builderClass.getSuperclass());
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
