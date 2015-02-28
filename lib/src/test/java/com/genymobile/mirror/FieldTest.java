package com.genymobile.mirror;

import com.genymobile.mirror.exception.MirrorException;
import com.genymobile.mirror.mock.PublicDummy;
import com.genymobile.mirror.target.PublicDummyClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    public void checkThatGettingAnExistingFieldReturnsProperObject() {
        String field = dummy.readField();

        assert(field.equals("iam field"));
    }

    @Test
    public void checkThatGettingANoNExistingFieldThrowException() {
        expectedException.expect(MirrorException.class);
        expectedException.expectMessage("Error while trying to get field.");

        dummy.readUnknownField();
    }
}
