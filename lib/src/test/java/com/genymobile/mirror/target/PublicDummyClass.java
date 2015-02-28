package com.genymobile.mirror.target;

public class PublicDummyClass {

    private String field;

    public PublicDummyClass() {

    }

    private String getString(int i) {
        return "Hello World!";
    }

    private static String getStaticString(char a) {
        return Character.toString(a);
    }
}
