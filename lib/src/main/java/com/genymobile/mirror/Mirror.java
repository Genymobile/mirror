package com.genymobile.mirror;

import java.lang.reflect.Proxy;

public final class Mirror {

    private static final Wrapper WRAPPER;
    private static final Unwrapper UNWRAPPER;

    static {
        WRAPPER = new Wrapper();
        UNWRAPPER = new Unwrapper();
    }

    public static <T> T create(java.lang.Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[] {clazz},
                new MirrorHandler(clazz, WRAPPER, UNWRAPPER));
    }

    private Mirror() {
        // don't intantiate
    }
}
