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

public class MirrorTest {

    private PrivateDummy dummy;
    private PublicDummy publicDummy;

    @Before
    public void init() {
        dummy = Mirror.create(PrivateDummy.class);
        publicDummy = Mirror.create(PublicDummy.class);
    }

    @Test
    public void checkThatDummyClassIsNotNull() {
        assertThat(dummy).isNotNull();
    }

    @Test
    public void checkThatCallingConstructReturnAnNonNullInstance() {
        Object object = dummy.construct("yolo");

        assertThat(object).isNotNull();
    }

    @Test
    public void checkThatCallingConstructReturnAValidInstance() {
        Object object = dummy.construct("yolo");

        Class<?> objectClass = object.getClass();
        assertThat(objectClass.getName()).isEqualTo("com.genymobile.mirror.target.PrivateDummyClass");
    }

    @Test
    public void checkThatCallingSetInstanceWithCorrectObjectSucceed() {
        PublicDummyClass instance = new PublicDummyClass();

        publicDummy.setInstance(instance);
    }


    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Test
    public void checkThatCallingSetInstanceWithWrongObjectFail() {
        Object instance = new Object();

        expectedException.expect(MirrorException.class);
        expectedException.expectMessage("Class doesn't match");
        publicDummy.setInstance(instance);
    }

    @Test
    public void checkThatCallingGetInstanceAfterSetInstanceReturnCorrectObject() {
        PublicDummyClass instance = new PublicDummyClass();

        publicDummy.setInstance(instance);

        assertThat(instance).isEqualTo(publicDummy.getInstance());
    }

    @Test
    public void checkThatCallingAMethodReturningAWrapperCorrectlyWrapTheResult() {
        PublicDummy result = dummy.getPublicDummyInstance();

        assertThat(result).isNotNull();
        Object instance = result.getInstance();
        assertThat(instance).isNotNull();
        assertThat(instance.getClass()).isEqualTo(PublicDummyClass.class);
    }
}
