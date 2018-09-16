package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.context.graph.Node;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.core.Parameterized;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
class ParametersMetaBuilder {
    private final InjectBuilder injectBuilder;

    List<Meta.Parameter> buildParameters(Executable executable, Context context, Set<Class<?>> staticsMembers) throws BuilderException {
        List<Meta.Parameter> metas = new ArrayList<>();
        java.lang.reflect.Parameter[] parameters = executable.getParameters();

        for (int i = 0; i < executable.getParameterCount(); i++) {
            Parameter type = parameters[i];

            Annotation qualifier = Annotations.findQualifier(type);
            Annotation scope = Annotations.findScope(type);
            metas.add(buildParameter(type, type.getParameterizedType(), qualifier, scope, context, staticsMembers));
        }

        if (executable.getParameterCount() != metas.size()) {
            throw new BuilderException("unknown error, haven't created the correct args: expected="
                    + Arrays.toString(executable.getParameters()) + ",created=" + metas);
        }
        return metas;
    }

    <T> Meta.Parameter buildParameter(AnnotatedElement annotatedElement, java.lang.reflect.Type type, Annotation qualifier,
                                      Annotation scope, Context context, Set<Class<?>> staticsMembers) throws BuilderException {
        Type<?> paramType = new Type<Object>(type) {};

        if (paramType.isProvider()) {
            if (!paramType.isParameterized()) {
                throw new BuilderException("non-parameterized provider: " + type);
            }

            Parameterized<?> parameterized = paramType.getParameterized();
            List<Type<?>> generics = parameterized.getGenerics();
            Type<?> providerType = generics.get(0);

            return Meta.ProviderParameter.<T>builder()
                   .type(providerType)
                   .inject(injectBuilder.build(annotatedElement))
                   .build();
        } else {
            Meta.InstanceParameter.Builder builder = Meta.InstanceParameter.builder();
            Node<?> node;
            try {
                node = context.lookup(paramType, qualifier, scope, staticsMembers);
            } catch (ContextException e) {
                throw new BuilderException("error looking up type: " + type + ": qualifier: " + qualifier, e);
            }
            builder.meta(node.getMeta());
            builder.inject(injectBuilder.build(annotatedElement));
            return builder.build();
        }
    }
}
