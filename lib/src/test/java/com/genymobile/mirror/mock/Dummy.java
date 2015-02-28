package com.genymobile.mirror.mock;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;

@Class("com.genymobile.mirror.taget.DummyClass")
public interface Dummy {

    @Constructor
    void construct(String string);

}
