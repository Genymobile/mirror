package com.genymobile.mirror;

import com.genymobile.mirror.exception.MirrorException;
import com.genymobile.mirror.mock.PrivateDummy;
import com.genymobile.mirror.mock.PublicDummy;
import com.genymobile.mirror.target.PublicDummyClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
        assert(dummy != null);
    }

    @Test
    public void checkThatCallingConstructReturnAnNonNullInstance() {
        Object object = dummy.construct("yolo");

        assert(object != null);
    }

    @Test
    public void checkThatCallingConstructReturnAValidInstance() {
        Object object = dummy.construct("yolo");

        assert(object.getClass().getName().equals("com.genymobile.mirror.target.PrivateDummyClass"));
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

        assert(instance == publicDummy.getInstance());
    }
}
