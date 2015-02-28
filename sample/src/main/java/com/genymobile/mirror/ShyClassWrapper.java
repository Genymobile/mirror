package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;
import com.genymobile.mirror.annotation.SetField;

@Class("com.genymobile.mirror.ShyClass")
public interface ShyClassWrapper {

    @SetField("privateField")
    void setPrivateField(int value);

    @Constructor
    Object buildShyClass(int value);

    String sumUp(int value);

    String privateFieldToString();
}
