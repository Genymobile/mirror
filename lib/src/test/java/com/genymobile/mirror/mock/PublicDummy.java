package com.genymobile.mirror.mock;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.GetField;
import com.genymobile.mirror.annotation.GetInstance;
import com.genymobile.mirror.annotation.SetField;
import com.genymobile.mirror.annotation.SetInstance;

@Class("com.genymobile.mirror.target.PublicDummyClass")
public interface PublicDummy {

    @GetInstance
    Object getInstance();

    @SetInstance
    void setInstance(Object instance);

    @SetField("field")
    void setField(String string);

    @SetField("do_not_exist")
    void setDoNotExist(String string);

    @GetField("field")
    String readField();

    @GetField("array")
    String[] readArray();

    @GetField("dummyArray")
    PrivateDummy[] readWrappedArray();

    @GetField("unknownField")
    Object readUnknownField();

    String getString(int i);

    String getStaticString(char a);

    PrivateDummy[] getDummyArray();

    // Do not exist
    Object throwExceptionMethod(String string);

    void doStuff(PrivateDummy privateDummy);

    int unwrapParametersAndReturnArraySize(PrivateDummy[] privateDummies);

    int doNotUnwrapPrimiteAndReturnArraySize(long[] privateDummies);
}
