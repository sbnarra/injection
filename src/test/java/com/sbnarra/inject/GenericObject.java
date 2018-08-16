package com.sbnarra.inject;

public abstract class GenericObject<T> {

    @TestIt.MyAn
    public void doSomething() {
        L.log("doing something");
    }
}
