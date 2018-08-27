package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.AnnotationsException;
import com.sbnarra.inject.graph.Node;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class FieldBuilder {
    private final Annotations annotations;

    List<Meta.Field> build(Meta.Class classMeta, Context context) throws BuilderException {
        Class bClass = classMeta.getContractClass();
        List<Meta.Field> metas = new ArrayList<>();
        for (Field field : bClass.getDeclaredFields()) {
            if (requiresInjection(field)) {
                metas.add(createFieldMeta(field, context));
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

    private Meta.Field createFieldMeta(Field field, Context context) throws BuilderException {
        String named;
        try {
            named = annotations.getName(field.getDeclaredAnnotations());
        } catch (AnnotationsException e) {
            throw new BuilderException("error finding field name: " + field, e);
        }

        Node<?> node;
        try {
            node = context.lookup(field.getType(), new Qualifier.Named(named));
        } catch (ContextException e) {
            throw new BuilderException("error looking up field in context: " + field, e);
        }

        Meta meta = node.getMeta();
        field.setAccessible(true);
        return Meta.Field.builder()
                .field(field)
                .meta(meta)
                .build();
    }
}
