package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.context.ContextException;
import io.github.sbnarra.injection.core.Annotations;
import io.github.sbnarra.injection.core.Debug;
import io.github.sbnarra.injection.core.Parameterized;
import io.github.sbnarra.injection.core.Type;
import io.github.sbnarra.injection.graph.Node;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
class ParametersMetaBuilder {
    private final InjectBuilder injectBuilder;

    List<Meta.Parameter> getParameters(Executable executable, Context context) throws BuilderException {
        List<Meta.Parameter> metas = new ArrayList<>();
        Annotation[][] annotations = executable.getParameterAnnotations();
        java.lang.reflect.Parameter[] parameters = executable.getParameters();

        for (int i = 0; i < executable.getParameterCount(); i++) {
            java.lang.reflect.Parameter type = parameters[i];
            Named named = Annotations.getName(annotations[i]);
            Debug.log(executable);
            Debug.log(type);
            Debug.log(named);

            try {
                Type<?> paramType = new Type<Object>(type.getParameterizedType()) {};
                Debug.log(paramType);
                Meta.Parameter.ParameterBuilder builder = Meta.Parameter.builder().provider(paramType.isProvider());

                if (paramType.isProvider()) {
                    Parameterized<?> parameterized = paramType.getParameterized();
                    List<Type<?>> generics = parameterized.getGenerics();
                    Type<?> providerType = generics.get(0);
                    // TODO - causing circlar dep
                    Node<?> node = context.lookup(providerType, named);

                   builder.meta(node.getMeta());
                } else {
                    Node<?> node = context.lookup(paramType, named);
                    builder.meta(node.getMeta());
                }
                builder.inject(injectBuilder.build(type));
                metas.add(builder.build());
            } catch (ContextException e) {
                throw new BuilderException("error looking up type: " + type + ": named: " + named != null ? named.value() : null, e);
            }
        }

        if (executable.getParameterCount() != metas.size()) {
            throw new BuilderException("unknown error, haven't created the correct args: expected="
                    + Arrays.toString(executable.getParameters()) + ",created=" + metas);
        }
        return metas;
    }
}
