package com.sbnarra.inject.meta.builder;

import com.sbnarra.inject.Debug;
import com.sbnarra.inject.context.Context;
import com.sbnarra.inject.context.ContextException;
import com.sbnarra.inject.core.Annotations;
import com.sbnarra.inject.core.AnnotationsException;
import com.sbnarra.inject.core.Parameterized;
import com.sbnarra.inject.core.Type;
import com.sbnarra.inject.graph.Node;
import com.sbnarra.inject.meta.Meta;
import com.sbnarra.inject.meta.Qualifier;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
class ParametersMetaBuilder {
    private final Annotations annotations;

    List<Meta.Parameter> getParameters(Executable executable, Context context) throws BuilderException {
        List<Meta.Parameter> metas = new ArrayList<>();
        Annotation[][] annotations = executable.getParameterAnnotations();
        java.lang.reflect.Parameter[] parameters = executable.getParameters();

        for (int i = 0; i < executable.getParameterCount(); i++) {
            java.lang.reflect.Parameter type = parameters[i];


            Qualifier.Named named = null;
            try {
                String nameStr = this.annotations.getName(annotations[i]);
                if (nameStr != null && !nameStr.isEmpty()) {
                    named = new Qualifier.Named(nameStr);
                }
            } catch (AnnotationsException e) {
                throw new BuilderException("error finding parameter name", e);
            }

            try {
                Type<?> paramType = new Type<Object>(type.getParameterizedType()) {};
                Meta.Parameter.ParameterBuilder builder = Meta.Parameter.builder().useProvider(paramType.isProvider());

                if (paramType.isProvider()) {
                    Parameterized<?> parameterized = paramType.getParameterized();
                    List<Type<?>> generics = parameterized.getGenerics();
                    Debug.log(generics);
                    Type<?> providerType = generics.get(0);
                    Node<?> node = context.lookup(providerType, named);

                   builder.meta(node.getMeta());
                } else {
                    Node<?> node = context.lookup(paramType, named);
                    builder.meta(node.getMeta());
                }
                metas.add(builder.build());
            } catch (ContextException e) {
                throw new BuilderException("error looking up type: " + type + ": named: " + named, e);
            }
        }

        if (executable.getParameterCount() != metas.size()) {
            throw new BuilderException("unknown error, haven't created the correct args: expected="
                    + Arrays.toString(executable.getParameters()) + ",created=" + metas);
        }
        return metas;
    }
}
