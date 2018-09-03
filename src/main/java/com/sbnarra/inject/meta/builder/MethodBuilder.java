package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.meta.Meta;

import java.util.List;
import java.util.stream.Collectors;

class MethodBuilder extends AbstractBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    public MethodBuilder(Annotations annotations, ParametersMetaBuilder parametersMetaBuilder) {
        super(annotations);
        this.parametersMetaBuilder = parametersMetaBuilder;
    }

    List<Meta.Method> build(Meta.Class classMeta, Context context) throws BuilderException {
        Class bClass = classMeta.getContractClass();
        try {
            return findInject(bClass.getDeclaredMethods()).stream()
                    .map(m -> {
                        try {
                            m.setAccessible(true);
                            return Meta.Method.builder()
                                    .method(m)
                                    .parameters(parametersMetaBuilder.getParameters(m, context))
                                    .build();
                        } catch (BuilderException e) {
                            throw new UncaughtBuilderException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (UncaughtBuilderException e) {
            throw e.builderException();
        }
    }

    private class UncaughtBuilderException extends RuntimeException {
        private BuilderException e;
        UncaughtBuilderException(BuilderException e) {
            super(e);
        }
        BuilderException builderException() {
            return e;
        }
    }
}
