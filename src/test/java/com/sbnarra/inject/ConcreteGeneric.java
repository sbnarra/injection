package com.sbnarra.inject;

import javax.inject.Inject;
import javax.inject.Named;

@TestIt.MyAn
public class ConcreteGeneric extends GenericObject<String> {

    @Inject
    @Named("a")
    String jds;

    @Override
    @TestIt.MyAn
    public void message(String ff) {
        Debug.log(hashCode() + " - " + jds + ":ConcreteGeneric doing something: " + ff);
    }
}
