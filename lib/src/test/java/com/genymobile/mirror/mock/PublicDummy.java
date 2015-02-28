package com.genymobile.mirror.mock;

import com.genymobile.mirror.annotation.*;
import com.genymobile.mirror.annotation.Class;

@Class("com.genymobile.mirror.target.PublicDummyClass")
public interface PublicDummy {

    @SetInstance
    void setInstance(Object instance);

    @SetField("field")
    void setField(String string);

    @SetField("do_not_exist")
    void setDoNotExist(String string);

    String getString(int i);

    // Do not exist
    Object throwExceptionMethod(String string);
}
