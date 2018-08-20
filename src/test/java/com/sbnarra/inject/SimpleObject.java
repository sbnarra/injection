package com.sbnarra.inject;

import java.util.ArrayList;
import java.util.List;

public class SimpleObject {

    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("hello");
        System.out.println(strings.contains("hello"));

    }

    @TestIt.MyAn
    public void print() {
        Debug.log("com.sbnarra.inject.SimpleObject Printing");
    }
}
