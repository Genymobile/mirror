package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.exception.MirrorException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/* package */ class Unwrapper {

    public static Object unwrap(Object object) {
        if (object == null) return null;
        return object.getClass().isArray() ?
                unwrapArray(object) :
                unwrapObject(object);
    }

    private static Object unwrapArray(Object object) {
        if (object.getClass().getComponentType().isPrimitive()) return object;
        Object[] objects = (Object[]) object;
        Object[] result = (Object[]) Array.newInstance(unwrapSimpleClass(objects.getClass().getComponentType()), objects.length);
        for (int i = 0; i < result.length; ++i) {
            result[i] = unwrap(objects[i]);
        }
        return result;
    }

    private static Object unwrapObject(Object object) {
        if (object != null && Proxy.isProxyClass(object.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
            if (invocationHandler instanceof MirrorHandler) {
                return ((MirrorHandler) invocationHandler).getInstance();
            }
        }
        return object;
    }

    public static java.lang.Class unwrapClass(java.lang.Class clazz) {
        if (clazz.isPrimitive()) {
            return clazz;
        }
        return clazz.isArray() ?
                unwrapArrayClass(clazz) :
                unwrapSimpleClass(clazz);
    }

    private static java.lang.Class unwrapSimpleClass(java.lang.Class clazz) {
        com.genymobile.mirror.annotation.Class annotation = (Class) clazz.getAnnotation(Class.class);
        if (annotation != null) {
            try {
                return java.lang.Class.forName(annotation.value());
            } catch (ClassNotFoundException e) {
                throw new MirrorException("Cannot find class for this type.", e);
            }
        }
        return clazz;
    }

    private static java.lang.Class unwrapArrayClass(java.lang.Class clazz) {
        return Array.newInstance(unwrapClass(clazz.getComponentType()), 0).getClass();
    }
}
