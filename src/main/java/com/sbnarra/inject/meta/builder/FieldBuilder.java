package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.InjectException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.graph.Graph;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import com.sbnarra.inject.registry.Registry;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class FieldBuilder {

    private final Annotations annotations;
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Field> build(Meta.Class classMeta, Graph graph, Registry registry) throws InjectException {
        Class bClass = classMeta.getContractClass();
        List<Meta.Field> metas = new ArrayList<>();
        for (Field field : bClass.getDeclaredFields()) {
            if (requiresInjection(field)) {
                metas.add(createFieldMeta(field, graph, registry));
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

    private Meta.Field createFieldMeta(Field field, Graph graph, Registry registry) throws InjectException {
        String named = annotations.getName(field.getDeclaredAnnotations());
        Meta meta = parametersMetaBuilder.getParameter(field.getDeclaringClass(), new Qualifier.Named(named), graph, registry);
        return Meta.Field.builder()
                .field(field)
                .meta(meta)
                .build();
    }
}
