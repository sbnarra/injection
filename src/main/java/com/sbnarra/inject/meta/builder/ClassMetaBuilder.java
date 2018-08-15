package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.meta.ClassMeta;
import com.sbnarra.inject.registry.Type;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.ByteBuddy;

@RequiredArgsConstructor
class ClassMetaBuilder {

    private final ByteBuddy byteBuddy;

    <T> ClassMeta resolve(Type<T> type) throws InjectException {
        ClassMeta.ClassMetaBuilder builder = ClassMeta.builder();
        if (type.getParameterized() != null) {
            resolveType(type, builder);
        } else {
            builder.bindClass(type.getClazz().getTheClass());
        }
        return builder.build();
    }

    private void resolveType(Type<?> type, ClassMeta.ClassMetaBuilder metaBuilder) {
        Class<?> builderClass = byteBuddy
                .subclass(type.getParameterized().getType())
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        metaBuilder.buildClass(builderClass);
        metaBuilder.bindClass(builderClass.getSuperclass());
    }
}
