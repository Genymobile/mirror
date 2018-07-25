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
package com.genymobile.mirror;

import com.genymobile.mirror.exception.MirrorException;
import com.genymobile.mirror.mock.PrivateDummy;
import com.genymobile.mirror.mock.PublicDummy;
import com.genymobile.mirror.target.PublicDummyClass;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodTest {

    private PublicDummy dummy;

    @Before
    public void init() {
        PublicDummyClass dummyClass = new PublicDummyClass();
        dummy = Mirror.create(PublicDummy.class);
        dummy.setInstance(dummyClass);
    }

    @Test
    public void checkThatCallingAMethodIsReturningExpectedObject() {
        String result = dummy.getString(890);

        assertThat(result).isEqualTo("Hello World!");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Test
    public void checkThatCallingAWrongMethodThrowsAnException() {
        expectedException.expect(MirrorException.class);
        expectedException.expectMessage("Error while trying to invoke method");

        dummy.throwExceptionMethod(":(");
    }

    @Test
    public void checkThatCallingAStaticMethodIsWorking() {
        dummy.setInstance(null);

        String result = dummy.getStaticString('b');

        assertThat(result).isEqualTo("b");
    }

    @Test
    public void checkThatInstanceIsRetrievedWhenPassingWrapper() {
        PrivateDummy privateDummy = Mirror.create(PrivateDummy.class);
        privateDummy.construct("foo");

        dummy.doStuff(privateDummy);
    }

    @Test
    public void checkThatReturnedArraysAreCorrect() {
        PrivateDummy[] array = dummy.getDummyArray();

        assertThat(array.getClass()).isEqualTo(PrivateDummy[].class);
        assertThat(array).hasSize(2);
    }

    @Test
    public void checkThatPassingWrappedObjectAsParameterUnwrapThem() {
        PrivateDummy[] array = new PrivateDummy[5];
        int result = dummy.unwrapParametersAndReturnArraySize(array);

        assertThat(result).isEqualTo(5);
    }

    @Test
    public void checkThatPassingPrimitiveAsParameterDoNotUnwrapThem() {
        long[] array = new long[5];
        int result = dummy.doNotUnwrapPrimiteAndReturnArraySize(array);

        assertThat(result).isEqualTo(5);
    }

    @Test
    public void checkThatCallingObjectMethodWorks() {
        String result = dummy.toString();
        assertThat(result).isNotEmpty();
    }
}
