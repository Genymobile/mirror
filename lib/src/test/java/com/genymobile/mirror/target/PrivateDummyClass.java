package com.genymobile.mirror.target;

public class PrivateDummyClass {

    protected PrivateDummyClass() {
    }

    private PrivateDummyClass(String string) {
    }

    private static PublicDummyClass getPublicDummyInstance() {
        return new PublicDummyClass();
    }
}
