package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.InjectException;
import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.core.AnnotationsException;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class FieldBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Field> build(Meta.Class<?> classMeta, Context context) throws BuilderException {
        Class<?> bClass = classMeta.getContractClass();
        List<Meta.Field> metas = new ArrayList<>();
        return build(bClass, context, metas);
    }

    private List<Meta.Field> build(Class<?> theClass, Context context, List<Meta.Field> fields) throws BuilderException {
        for (Field field : theClass.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                fields.add(createFieldMeta(field, context));
            }
        }

        if (theClass.getSuperclass() != null) {
            return build(theClass.getSuperclass(), context, fields);
        }
        return fields;
    }

    private Meta.Field createFieldMeta(Field field, Context context) throws BuilderException {
        field.setAccessible(true);
        try {
            return Meta.Field.builder()
                    .field(field)
                    .parameter(parametersMetaBuilder.getParameter(field, field.getGenericType(), Annotations.findQualifier(field), Annotations.findScope(field), context))
                    .build();
        } catch (AnnotationsException e) {
            throw new BuilderException("error finding qualifier", e);
        }
    }
}
