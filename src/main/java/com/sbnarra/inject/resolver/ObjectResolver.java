package com.sbnarra.inject.resolver;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectResolver {

    private final ClassResolver classResolver;
    private final ConstructorResolver constructorResolver;
    private final MethodResolver methodResolver;

    public ObjectMeta resolve(Type<?> type) throws InjectException {
        ClassMeta classMeta = classResolver.resolve(type);
        return ObjectMeta.builder()
                .classMeta(classMeta)
                .constructorMeta(constructorResolver.resolve(classMeta))

                .fieldMeta(null)
                .methodMeta(methodResolver.resolve(classMeta))
                .build();
    }
}
