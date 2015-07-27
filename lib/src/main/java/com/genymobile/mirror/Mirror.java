package com.genymobile.mirror;

import java.lang.reflect.Proxy;

public class Mirror {

    private static final Wrapper wrapper;
    private static final Unwrapper unwrapper;

    static {
        wrapper = new Wrapper();
        unwrapper = new Unwrapper();
    }

    public static <T> T create(java.lang.Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[] {clazz},
                new MirrorHandler(clazz, wrapper, unwrapper));
    }
}
