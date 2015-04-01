package com.genymobile.mirror.mock;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;
import com.genymobile.mirror.annotation.SetInstance;
import com.genymobile.mirror.target.PublicDummyClass;

@Class("com.genymobile.mirror.target.PrivateDummyClass")
public interface PrivateDummy {

    @Constructor
    Object construct(String string);

    PublicDummy getPublicDummyInstance();
}
