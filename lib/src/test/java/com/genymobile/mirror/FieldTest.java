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

public class FieldTest {

    private PublicDummy dummy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() {
        PublicDummyClass dummyClass = new PublicDummyClass();
        dummy = Mirror.create(PublicDummy.class);
        dummy.setInstance(dummyClass);
    }

    @Test
    public void checkThatSettingAnExistingFieldDoNotThrowsExceptions() {
        dummy.setField("Hotline Miami");
    }

    @Test
    public void checkThatSettingANoNExistingFieldThrowException() {
        expectedException.expect(MirrorException.class);
        expectedException.expectMessage("Error while trying to set field.");

        dummy.setDoNotExist("Oh noes!");
    }

    @Test
    public void checkThatGettingAnExistingFieldDoNotThrowsExceptions() {
        Object object = dummy.readField();
    }

    @Test
    public void checkThatRetrievingAnArrayReturnsAnArray() {
        Object array = dummy.readArray();

        Class<?> arrayClass = array.getClass();
        assertThat(arrayClass.isArray()).isTrue();
    }

    @Test
    public void checkThatRetrievingAnArrayWrapsObjects() {
        Object array = dummy.readWrappedArray();

        Class<?> arrayClass = array.getClass();
        assertThat(arrayClass.isArray()).isTrue();
        assertThat(arrayClass.getComponentType()).isEqualTo(PrivateDummy.class);
        assertThat(((Object[]) array)).hasSize(2);
    }

    @Test
    public void checkThatGettingAnExistingFieldReturnsProperObject() {
        String field = dummy.readField();

        assertThat(field).isEqualTo("iam field");
    }

    @Test
    public void checkThatGettingANoNExistingFieldThrowException() {
        expectedException.expect(MirrorException.class);
        expectedException.expectMessage("Error while trying to get field.");

        dummy.readUnknownField();
    }
}
