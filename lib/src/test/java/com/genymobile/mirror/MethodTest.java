package com.genymobile.mirror;

import com.genymobile.mirror.exception.MirrorException;
import com.genymobile.mirror.mock.PrivateDummy;
import com.genymobile.mirror.mock.PublicDummy;
import com.genymobile.mirror.target.PublicDummyClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

        assert(result.equals("Hello World!"));
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Test
    public void checkThatCallingAWrongMethodThrowsAnException() {
        expectedException.expect(MirrorException.class);
        expectedException.expectMessage("Error while trying to invoke method");

        dummy.throwExceptionMethod(":(");
    }
}
