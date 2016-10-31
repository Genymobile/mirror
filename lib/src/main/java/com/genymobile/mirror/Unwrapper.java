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
