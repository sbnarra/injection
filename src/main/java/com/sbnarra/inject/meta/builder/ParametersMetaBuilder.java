package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.Context;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class ParametersMetaBuilder {
    private final Annotations annotations;

    List<Meta> getParameters(Executable executable, Context context) throws InjectException {
        List<Meta> metas = new ArrayList<>();
        Annotation[][] annotations = executable.getParameterAnnotations();
        Class<?>[] types = executable.getParameterTypes();
        for (int i = 0; i < executable.getParameterCount(); i++) {
            Class<?> type = types[i];
            String named = this.annotations.getName(annotations[i]);
            metas.add(context.lookup(type, new Qualifier.Named(named)).getMeta());
        }
        return metas;
    }
}
