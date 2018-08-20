package com.sbnarra.inject;

public abstract class GenericObject<T> {

    @TestIt.MyAn
    public void message(String dd) {
        Debug.log(hashCode() + "doing something");
    }
}
