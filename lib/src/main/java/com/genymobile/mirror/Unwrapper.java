package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.exception.MirrorException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Utility class helping unwrapping instances from proxies
 */
class Unwrapper {

    private final ClassLoader classLoader;

    Unwrapper(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Object unwrap(Object object) {
        if (object == null) {
            return null;
        }
        return object.getClass().isArray()
                ? unwrapArray(object)
                : unwrapObject(object);
    }

    private Object unwrapArray(Object object) {
        java.lang.Class componentType = object.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            return object;
        }
        Object[] objects = (Object[]) object;
        Object[] result = (Object[]) Array.newInstance(unwrapSimpleClass(componentType), objects.length);
        for (int i = 0; i < result.length; ++i) {
            result[i] = unwrap(objects[i]);
        }
        return result;
    }

    private Object unwrapObject(Object object) {
        if (object != null && Proxy.isProxyClass(object.getClass())) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
            if (invocationHandler instanceof MirrorHandler) {
                return ((MirrorHandler) invocationHandler).getTargetInstance();
            }
        }
        return object;
    }

    public java.lang.Class unwrapClass(java.lang.Class clazz) {
        if (clazz.isPrimitive()) {
            return clazz;
        }
        return clazz.isArray()
                ? unwrapArrayClass(clazz)
                : unwrapSimpleClass(clazz);
    }

    private java.lang.Class unwrapSimpleClass(java.lang.Class clazz) {
        com.genymobile.mirror.annotation.Class annotation = (Class) clazz.getAnnotation(Class.class);
        if (annotation != null) {
            try {
                return java.lang.Class.forName(annotation.value(), true, classLoader);
            } catch (ClassNotFoundException e) {
                throw new MirrorException("Cannot find class for this type.", e);
            }
        }
        return clazz;
    }

    private java.lang.Class unwrapArrayClass(java.lang.Class clazz) {
        return Array.newInstance(unwrapClass(clazz.getComponentType()), 0).getClass();
    }
}
