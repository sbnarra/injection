package com.sbnarra.inject.resolver;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.attribute.AnnotationRetention;

public class ResolverFactory {

    public ClassResolver get() {
        ByteBuddy byteBuddy = new ByteBuddy().with(AnnotationRetention.ENABLED);

        ConstructorResolver constructorResolver = new ConstructorResolver();
        FieldResolver fieldResolver = new FieldResolver();
        MethodResolver methodResolver = new MethodResolver();

        ClassResolver classResolver = new ClassResolver(byteBuddy, constructorResolver, methodResolver, fieldResolver);

        return classResolver;
    }
}
