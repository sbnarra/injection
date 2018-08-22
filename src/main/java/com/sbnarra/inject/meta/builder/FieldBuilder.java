package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.Context;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class FieldBuilder {
    private final Annotations annotations;

    List<Meta.Field> build(Meta.Class classMeta, Context context) throws InjectException {
        Class bClass = classMeta.getContractClass();
        List<Meta.Field> metas = new ArrayList<>();
        for (Field field : bClass.getDeclaredFields()) {
            if (requiresInjection(field)) {
                try {
                    metas.add(createFieldMeta(field, context));
                } catch (InjectException e) {
                    throw new InjectException("failed to inject field: " + field, e);
                }
            }
        }
        return metas;
    }

    private boolean requiresInjection(Field field) {
        for (Class injectAnnotationClass : annotations.getInject()) {
            if (field.getAnnotation(injectAnnotationClass) != null) {
                return true;
            }
        }
        return false;
    }

    private Meta.Field createFieldMeta(Field field, Context context) throws InjectException {
        String named = annotations.getName(field.getDeclaredAnnotations());
        Debug.log("named: " + named);
        Meta meta = context.lookup(field.getDeclaringClass(), new Qualifier.Named(named)).getMeta();
        return Meta.Field.builder()
                .field(field)
                .meta(meta)
                .build();
    }
}
