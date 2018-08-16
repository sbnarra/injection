package com.sbnarra.inject;

public class ConcreteGeneric extends GenericObject<String> {

    @Override
    @TestIt.MyAn
    public void doSomething() {
        L.log("ConcreteGeneric doing something");
    }
}
