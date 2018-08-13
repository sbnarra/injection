package com.sbnarra.inject.injector;

import com.sbnarra.inject.registry.Type;
import net.bytebuddy.ByteBuddy;

public class ClassResolver {

    public <T> Class<T> tt(Type<T> type) {
        return new ByteBuddy()
            .subclass(type.getType())
                .constructor(null)
            .make()
            .load(getClass().getClassLoader())
                .getLoaded();
    }
}
