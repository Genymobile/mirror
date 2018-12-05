/*
 * Copyright (c) 2016 Genymobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.exception.MirrorException;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class helping wrapping object in a Proxy instance if needed
 */
class Wrapper {

    private final ClassLoader classLoader;
    private final ReflectionFinder finder;

    Wrapper(ClassLoader classLoader, ReflectionFinder finder) {
        this.classLoader = classLoader;
        this.finder = finder;
    }

    public Object wrap(java.lang.Class clazz, Object object) throws InvocationTargetException, IllegalAccessException {
        if (object == null) {
            return null;
        }
        return clazz.isArray()
                ? wrapArray(clazz.getComponentType(), object)
                : wrapObject(clazz, object);
    }

    private Object wrapArray(java.lang.Class clazz, Object object) throws InvocationTargetException, IllegalAccessException {
        if (object.getClass().getComponentType().isPrimitive()) {
            return object;
        }
        Object[] objects = (Object[]) object;
        Object[] results = (Object[]) Array.newInstance(clazz, objects.length);
        for (int i = 0; i < objects.length; ++i) {
            results[i] = wrap(clazz, objects[i]);
        }
        return results;
    }

    private Object wrapObject(java.lang.Class clazz, Object result) throws InvocationTargetException, IllegalAccessException {
        return isClassWrappable(clazz)
                ? createWrapperWithInstance(clazz, result)
                : result;
    }

    private boolean isClassWrappable(java.lang.Class clazz) {
        return clazz.getAnnotation(Class.class) != null;
    }

    private Object createWrapperWithInstance(java.lang.Class clazz, Object instance) throws InvocationTargetException, IllegalAccessException {
        Object object = Mirror.create(clazz, classLoader);
        Method setInstance = finder.findSetInstanceMethod(clazz);
        if (setInstance == null) {
            throw new MirrorException("The class " + clazz.getName()
                    + " has no @SetInstance method so we cannot wrap any result.");
        }
        setInstance.invoke(object, instance);
        return object;
    }

}
