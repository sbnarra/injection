package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.AnnotationsException;
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

    List<Meta> getParameters(Executable executable, Context context) throws BuilderException {
        List<Meta> metas = new ArrayList<>();
        Annotation[][] annotations = executable.getParameterAnnotations();
        Class<?>[] types = executable.getParameterTypes();
        for (int i = 0; i < executable.getParameterCount(); i++) {
            Class<?> type = types[i];
            String named;
            try {
                named = this.annotations.getName(annotations[i]);
            } catch (AnnotationsException e) {
                throw new BuilderException("error finding parameter name", e);
            }

            Meta<?> meta;
            try {
                meta = context.lookup(type, new Qualifier.Named(named)).getMeta();
            } catch (ContextException e) {
                throw new BuilderException("error looking up type: " + type + ": named: " + named, e);
            }

            metas.add(meta);
        }
        return metas;
    }
}
