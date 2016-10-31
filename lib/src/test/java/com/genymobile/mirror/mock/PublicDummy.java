/*
 * Copyright (c) 2016 Genymobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
