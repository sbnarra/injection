package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.core.AnnotationsException;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class FieldBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Field> build(Class<?> theClass, Context context) throws BuilderException {
        return build(theClass, context, new ArrayList<>());
    }

    private List<Meta.Field> build(Class<?> theClass, Context context, List<Meta.Field> fields) throws BuilderException {
        for (Field field : theClass.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Meta.Field fieldMeta = createFieldMeta(field, context);
                fields.add(fieldMeta);
            }
        }
        return fields;
    }

    private Meta.Field createFieldMeta(Field field, Context context) throws BuilderException {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }

        Annotation qualifier, scope;
        try {
            qualifier = Annotations.findQualifier(field);
            scope = Annotations.findScope(field);
        } catch (AnnotationsException e) {
            throw new BuilderException("error finding qualifier", e);
        }

        Meta.Parameter parameter = parametersMetaBuilder.buildParameter(field, field.getGenericType(), qualifier, scope, context);
        return Meta.Field.builder()
                .field(field)
                .parameter(parameter)
                .build();
    }
}
