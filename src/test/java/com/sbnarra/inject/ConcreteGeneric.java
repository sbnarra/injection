package com.sbnarra.inject;

@TestIt.MyAn
public class ConcreteGeneric extends GenericObject<String> {

    @Override
    @TestIt.MyAn
    public void message(String ff) {
        Debug.log(hashCode()+ "ConcreteGeneric doing something: " + ff);
    }
}
