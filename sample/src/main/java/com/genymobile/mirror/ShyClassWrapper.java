package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Field;

@Class("com.genymobile.mirror.ShyClass")
public interface ShyClassWrapper {

    @Field("privateField")
    int getPrivateField();


}
