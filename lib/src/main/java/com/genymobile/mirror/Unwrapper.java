package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.exception.MirrorException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/* package */ class Unwrapper {

    public static Object unwrap(Object object) {
        if (object.getClass().isArray()) {
            return object.getClass().getComponentType().isPrimitive() ?
                    object : unwrapArray((Object[]) object);
        }
        return unwrapObject(object);
    }

    private static Object unwrapArray(Object[] objects) {
        Object[] result = (Object[]) Array.newInstance(unwrapParameterType(objects.getClass().getComponentType()), objects.length);
        for (int i = 0; i < result.length; ++i) {
            result[i] = unwrapObject(objects[i]);
        }
        return result;
    }

    private static Object unwrapObject(Object object) {
        if (object == null) {
            return object;
        }
        if (Proxy.isProxyClass(object.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
            if (invocationHandler instanceof MirrorHandler) {
                return ((MirrorHandler) invocationHandler).getInstance();
            }
        }
        return object;
    }

    public static java.lang.Class unwrapParameter(java.lang.Class clazz) {
        return clazz.isArray() ?
                unwrapParameterArray(clazz) :
                unwrapParameterType(clazz);
    }

    private static java.lang.Class unwrapParameterType(java.lang.Class clazz) {
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

    private static java.lang.Class unwrapParameterArray(java.lang.Class clazz) {
        if (clazz.getComponentType().isPrimitive()) {
            return clazz;
        }
        return Array.newInstance(unwrapParameterType(clazz.getComponentType()), 0).getClass();
    }

}
