package com.genymobile.mirror.mock;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;

@Class("com.genymobile.mirror.target.DummyClass")
public interface Dummy {

    @Constructor
    Object construct(String string);

}
