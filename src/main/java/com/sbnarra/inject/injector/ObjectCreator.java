package com.sbnarra.inject.injector;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.resolver.ObjectResolver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjectCreator {

    private final ObjectResolver objectResolver;

    public <T> T create(ClassMeta classMeta) throws InjectException {
        return null;
    }
}
