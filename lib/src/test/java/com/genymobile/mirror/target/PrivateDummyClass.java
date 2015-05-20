package com.genymobile.mirror.target;

import com.genymobile.mirror.mock.PublicDummy;

public class PrivateDummyClass {

    private PrivateDummyClass(String string) {

    }

    private static PublicDummyClass getPublicDummyInstance() {
        return new PublicDummyClass();
    }
}
