package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.meta.ObjectMeta;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectMetaBuilder {

    private final ClassMetaBuilder classMetaBuilder;
    private final ConstructorMetaBuilder constructorMetaBuilder;
    private final MethodMetaBuilder methodMetaBuilder;

    public ObjectMeta resolve(Type<?> type) throws InjectException {
        ClassMeta classMeta = classMetaBuilder.resolve(type);
        return ObjectMeta.builder()
                .classMeta(classMeta)
                .constructorMeta(constructorMetaBuilder.resolve(classMeta))

                .fieldMeta(null)
                .methodMeta(methodMetaBuilder.resolve(classMeta))
                .build();
    }
}
