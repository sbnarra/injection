package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class MethodBuilder {
    private final ParametersMetaBuilder parametersMetaBuilder;

    List<Meta.Method> build(Meta.Class<?> classMeta, Context context) throws BuilderException {
        Class<?> bClass = classMeta.getContractClass();
        try {
            return Annotations.findInject(bClass.getDeclaredMethods()).stream()
                    .map(m -> {
                        try {
                            m.setAccessible(true);
                            return Meta.Method.builder()
                                    .method(m)
                                    .parameters(parametersMetaBuilder.getParameters(m, context))
                                    .build();
                        } catch (BuilderException e) {
                            throw e.unchecked();
                        }
                    })
                    .collect(Collectors.toList());
        } catch (BuilderException.Unchecked e) {
            throw e.builderException();
        }
    }
}
