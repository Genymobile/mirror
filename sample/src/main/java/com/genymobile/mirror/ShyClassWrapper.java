package com.genymobile.mirror;

import com.genymobile.mirror.Class;

@Class("com.genymobile.mirror.ShyClass")
public interface ShyClassWrapper {

    @Field("privateField")
    int getPrivateField();
}
