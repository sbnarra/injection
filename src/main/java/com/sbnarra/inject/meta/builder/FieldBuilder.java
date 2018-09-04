package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.graph.Node;
import com.sbnarra.inject.meta.Meta;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class FieldBuilder {
    private final InjectBuilder injectBuilder;

    List<Meta.Field> build(Meta.Class<?> classMeta, Context context) throws BuilderException {
        Class<?> bClass = classMeta.getContractClass();
        List<Meta.Field> metas = new ArrayList<>();
        for (Field field : bClass.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                metas.add(createFieldMeta(field, context));
            }
        }
        return metas;
    }

    private Meta.Field createFieldMeta(Field field, Context context) throws BuilderException {
        Named named = Annotations.getName(field.getDeclaredAnnotations());

        Node<?> node;
        try {
            node = context.lookup(field.getType(), named);

        } catch (ContextException e) {
            throw new BuilderException("error looking up field in context: " + field, e);
        }

        Meta<?> meta = node.getMeta();
        field.setAccessible(true);
        return Meta.Field.builder()
                .field(field)
                .meta(meta)
                .inject(injectBuilder.build(field))
                .build();
    }
}
