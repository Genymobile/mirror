package com.genymobile.mirror.mock;

import com.genymobile.mirror.annotation.*;
import com.genymobile.mirror.annotation.Class;

@Class("com.genymobile.mirror.target.PublicDummyClass")
public interface PublicDummy {
    @SetInstance
    void setInstance(Object instance);

}
