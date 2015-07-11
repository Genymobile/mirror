package com.genymobile.mirror;

import com.genymobile.mirror.annotation.*;
import com.genymobile.mirror.exception.MirrorDeveloperException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*package*/ class Wrapper {

    /* package */ static Object wrap(java.lang.Class clazz, Object result) throws InvocationTargetException, IllegalAccessException {
        if (clazz.isArray()) {
            return wrapArray(clazz.getComponentType(), (Object[]) result);
        }
        return wrapObject(clazz, result);
    }

    private static Object wrapArray(java.lang.Class clazz, Object[] objects) throws InvocationTargetException, IllegalAccessException {
        Object[] results = (Object[]) Array.newInstance(clazz, objects.length);
        for (int i = 0; i < objects.length; ++i) {
            results[i] = wrapObject(clazz, objects[i]);
        }
        return results;
    }

    private static Object wrapObject(java.lang.Class clazz, Object result) throws InvocationTargetException, IllegalAccessException {
        if (isWrapperClass(clazz)) {
            Object object = Mirror.create(clazz);
            Method setInstance = findSetInstanceMethod(clazz);
            setInstance.invoke(object, result);
            return object;
        } else {
            return result;
        }

    }

    private static boolean isWrapperClass(java.lang.Class clazz) {
        return clazz.getAnnotation(com.genymobile.mirror.annotation.Class.class) != null;
    }

    private static Method findSetInstanceMethod(java.lang.Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(SetInstance.class) != null) {
                return method;
            }
        }
        throw new MirrorDeveloperException("The class " + clazz.getName() + " has no setInstance() methods so we cannot wrap any result.");
    }

}
