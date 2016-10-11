package com.genymobile.mirror;

import java.lang.reflect.Proxy;

public final class Mirror {

    public static <T> T create(java.lang.Class<T> clazz) {
        return create(clazz, clazz.getClassLoader());
    }

    public static <T> T create(java.lang.Class<T> clazz, ClassLoader targetClassLoader) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new MirrorHandler(clazz, targetClassLoader, new Wrapper(targetClassLoader), new Unwrapper(targetClassLoader)));
    }

    private Mirror() {
        // don't intantiate
    }
}
