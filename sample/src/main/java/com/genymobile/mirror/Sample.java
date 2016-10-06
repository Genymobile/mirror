package com.genymobile.mirror;

public final class Sample {

    private Sample() {
        // don't intantiate
    }

    public static void main(String[] args) {

        System.out.println("Sample Application");

        System.out.println("Create wrapper");
        ShyClassWrapper shyClass = Mirror.create(ShyClassWrapper.class);

        System.out.println("Build ShyClass");
        shyClass.buildShyClass(18);

        String value;

        value = shyClass.privateFieldToString();
        System.out.println("Print privateField via public method: " + value);

        System.out.println("Set private field");
        shyClass.setPrivateField(10);

        value = shyClass.privateFieldToString();
        System.out.println("Print privateField via public method: " + value);

        value = shyClass.sumUp(32);
        System.out.println("Call private method: " + value);
    }
}
