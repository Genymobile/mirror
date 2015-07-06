package com.genymobile.mirror.target;

public class PublicDummyClass {

    private String field = "iam field";

    private String[] array = {};

    private PrivateDummyClass[] dummyArray = new PrivateDummyClass[] { new PrivateDummyClass(), new PrivateDummyClass()};

    public PublicDummyClass() {
    }

    private String getString(int i) {
        return "Hello World!";
    }

    private PrivateDummyClass[] getDummyArray() {
        return dummyArray;
    }

    private static String getStaticString(char a) {
        return Character.toString(a);
    }

    private void doStuff(PrivateDummyClass privateDummyClass) {
    }

    int unwrapParametersAndReturnArraySize(PrivateDummyClass[] privateDummies){
        return privateDummies.length;
    }
    int doNotUnwrapPrimiteAndReturnArraySize(long[] primitives){
        return primitives.length;
    }
}
