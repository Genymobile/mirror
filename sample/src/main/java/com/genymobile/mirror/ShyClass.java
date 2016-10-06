package com.genymobile.mirror;

public final class ShyClass {

    private int privateField;

    private ShyClass(int field) {
        this.privateField = field;
    }

    public String privateFieldToString() {
        return String.valueOf(privateField);
    }

    private String sumUp(int value) {
        return "The result is " + (privateField + value);
    }
}
