package com.sbnarra.inject.resolver;

import com.sbnarra.inject.InjectionAnnotations;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

@RequiredArgsConstructor
public class ResolverFactory {

    private final InjectionAnnotations injectionAnnotations;

    public ObjectResolver get() {
        ByteBuddy byteBuddy = new ByteBuddy().with(AnnotationRetention.ENABLED);

        ConstructorResolver constructorResolver = new ConstructorResolver();
        FieldResolver fieldResolver = new FieldResolver();
        MethodResolver methodResolver = new MethodResolver();

        ClassResolver classResolver = new ClassResolver(byteBuddy, constructorResolver, methodResolver, fieldResolver);

        return new ObjectResolver(
                new ClassResolver(new ByteBuddy().with(AnnotationRetention.ENABLED)),
                new ConstructorResolver(injectionAnnotations, objectResolver)
        );
    }
}
