package com.sbnarra.inject.injector;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.resolver.ClassResolver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectCreator {

    private final ClassResolver classResolver;

    public <T> T create(ClassMeta classMeta) throws InjectException {
        ClassMeta classMeta = classResolver.resolve(type);

        classMeta.getConstructorMeta()

        resolved.getBuilderClass().getConstructor().newInstance();
    }
}
