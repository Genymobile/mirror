package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.SetInstance;
import com.genymobile.mirror.exception.MirrorDeveloperException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class helping wrapping object in a Proxy instance if needed
 */
/*package*/ class Wrapper {

    /* package */ static Object wrap(java.lang.Class clazz, Object object) throws InvocationTargetException, IllegalAccessException {
        if (object == null) return null;
        if (clazz.isArray()) {
            return wrapArray(clazz.getComponentType(), object);
        }
        return wrapObject(clazz, object);
    }

    private static Object wrapArray(java.lang.Class clazz, Object object) throws InvocationTargetException, IllegalAccessException {
        if (object.getClass().getComponentType().isPrimitive()) return object;
        Object[] objects = (Object[]) object;
        Object[] results = (Object[]) Array.newInstance(clazz, objects.length);
        for (int i = 0; i < objects.length; ++i) {
            results[i] = wrap(clazz, objects[i]);
        }
        return results;
    }

    private static Object wrapObject(java.lang.Class clazz, Object result) throws InvocationTargetException, IllegalAccessException {
        return isClassWrappable(clazz) ?
                createWrapperWithInstance(clazz, result) :
                result;
    }

    private static boolean isClassWrappable(java.lang.Class clazz) {
        return clazz.getAnnotation(Class.class) != null;
    }

    private static Object createWrapperWithInstance(java.lang.Class clazz, Object instance) throws InvocationTargetException, IllegalAccessException {
        Object object = Mirror.create(clazz);
        Method setInstance = findSetInstanceMethod(clazz);
        setInstance.invoke(object, instance);
        return object;
    }

    private static Method findSetInstanceMethod(java.lang.Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (isSetInstanceMethods(method)) {
                return method;
            }
        }
        throw new MirrorDeveloperException("The class " + clazz.getName() + " has no setInstance() methods so we cannot wrap any result.");
    }

    private static boolean isSetInstanceMethods(Method method) {
        return method.getAnnotation(SetInstance.class) != null;
    }
}
