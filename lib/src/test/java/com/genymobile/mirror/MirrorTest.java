package com.genymobile.mirror;

import com.genymobile.mirror.mock.Dummy;
import org.junit.Before;
import org.junit.Test;

public class MirrorTest {

    private Dummy dummy;

    @Before
    public void init() {
        dummy = Mirror.create(Dummy.class);
    }

    @Test
    public void checkThatDummyClassIsNotNull() {
        assert(dummy != null);
    }
}
