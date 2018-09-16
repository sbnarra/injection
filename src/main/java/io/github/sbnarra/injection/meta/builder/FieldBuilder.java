package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
class FieldBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    void build(Class<?> theClass, Context context, List<Meta.Field> fields, Set<Class<?>> staticsMembers) throws BuilderException {
        for (Field field : theClass.getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Meta.Field fieldMeta = createFieldMeta(field, context, staticsMembers);
                if (Modifier.isStatic(field.getModifiers())) {
                    staticsMembers.add(theClass);
                } else {
                    fields.add(fieldMeta);
                }
            }
        }
    }

    private Meta.Field createFieldMeta(Field field, Context context, Set<Class<?>> staticsMembers) throws BuilderException {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }

        Annotation qualifier = Annotations.findQualifier(field);
        Annotation scope = Annotations.findScope(field);

        Meta.Parameter parameter = parametersMetaBuilder.buildParameter(field, field.getGenericType(), qualifier, scope, context, staticsMembers);
        return Meta.Field.builder()
                .field(field)
                .parameter(parameter)
                .build();
    }
}
