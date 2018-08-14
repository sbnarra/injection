package com.sbnarra.inject.resolver;

import com.sbnarra.inject.meta.ConstructorMeta;

import java.lang.reflect.Constructor;
import java.util.List;

class ConstructorResolver {


    ConstructorMeta resolve(List<Constructor> constructors) {

    }

    ConstructorMeta resolve(Constructor[] constructors) {
        Constructor constructor = find(constructors);
        for (Constructor constructor : constructors) {
            if (marked with inject annotation) {

            }
        }
        // get all constructors
        // check which are marked for injections
        // build parameter meta
        // return meta data

        ConstructorMeta constructorMeta = new ConstructorMeta();
        constructorMeta.getConstructor().getTypeParameters()
        return constructorMeta;
    }

    private Constructor find(Constructor[] constructors) {
        return null;
    }
}
