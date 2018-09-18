package io.github.sbnarra.injection.meta.builder;

import io.github.sbnarra.injection.context.Context;
import io.github.sbnarra.injection.meta.Meta;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class MembersBuilder {
    private final MethodBuilder methodBuilder;
    private final FieldBuilder fieldBuilder;

    List<Meta.Members> build(Class<?> theClass, Context context, Set<Class<?>> staticsMembers) throws BuilderException {
        List<Meta.Members> members = new ArrayList<>();
        build(theClass, context, members, new ArrayList<>(), new HashMap<>(), staticsMembers);
        return members;
    }

    private void build(Class<?> theClass, Context context, List<Meta.Members> members, List<Method> publicProtectedMethods,
                       Map<Package, List<Method>> defaultMethods, Set<Class<?>> staticsMembers) throws BuilderException {

        List<Meta.Field> fieldsMetas = new ArrayList<>();
        fieldBuilder.build(theClass, context, fieldsMetas, staticsMembers);

        List<Meta.Method> methodMetas = new ArrayList<>();
        methodBuilder.build(theClass, context, publicProtectedMethods, defaultMethods, methodMetas, staticsMembers);

        if (!fieldsMetas.isEmpty() || !methodMetas.isEmpty()) {
            Meta.Members membersMeta = Meta.Members.builder()
                    .theClass(theClass)
                    .fields(fieldsMetas).methods(methodMetas)
                    .build();
            members.add(membersMeta);
        }

        Class<?> superclass = theClass.getSuperclass();
        if (!Object.class.equals(superclass)) {
            build(superclass, context, members, publicProtectedMethods, defaultMethods, staticsMembers);
        }
    }
}
