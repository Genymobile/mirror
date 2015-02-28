package com.genymobile.mirror;

import java.lang.reflect.Proxy;

public class Mirror {

    public static <T> T create(java.lang.Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] {clazz}, new MirrorHandler(clazz));
    }
}
