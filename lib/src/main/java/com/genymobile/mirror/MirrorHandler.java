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
import com.genymobile.mirror.annotation.Constructor;
import com.genymobile.mirror.annotation.GetField;
import com.genymobile.mirror.annotation.GetInstance;
import com.genymobile.mirror.annotation.SetField;
import com.genymobile.mirror.annotation.SetInstance;
import com.genymobile.mirror.exception.MirrorException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MirrorHandler implements InvocationHandler {

    private final java.lang.Class<?> mirrorDefinition; //interface created by user
    private final Wrapper wrapper;
    private final Unwrapper unwrapper;
    private final ReflectionFinder finder;

    private java.lang.Class<?> clazz; //class to mirror
    private final ClassLoader classLoader; //classloader of the class to mirror
    private Object object; //target object

    public MirrorHandler(java.lang.Class<?> mirrorDefinition, ClassLoader targetClassLoader, Wrapper wrapper, Unwrapper unwrapper, ReflectionFinder finder) {
        this.mirrorDefinition = mirrorDefinition;
        this.classLoader = targetClassLoader;
        this.wrapper = wrapper;
        this.unwrapper = unwrapper;
        this.finder = finder;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        ensureClass();

        if (method.getAnnotation(Constructor.class) != null) {
            buildAndStoreInstance(method, args);
            return object;
        }

        if (method.getAnnotation(GetInstance.class) != null) {
            return object;
        }

        if (method.getAnnotation(SetInstance.class) != null) {
            if (args != null && args.length == 1) {
                this.object = args[0];
                ensureObjectClass();
                return this.object;
            } else {
                throw new MirrorException("Missing instance object");
            }
        }

        SetField setField = method.getAnnotation(SetField.class);
        if (setField != null) {
            setField(setField, args);
            return null;
        }

        GetField getField = method.getAnnotation(GetField.class);
        if (getField != null) {
            return getField(getField, method);
        }

        //no annotation found, try to call method
        return invokeMethod(method, args);
    }

    private Object getField(GetField getField, Method method) {
        try {
            Field fieldzz = finder.findField(clazz, getField.value());
            fieldzz.setAccessible(true);
            return wrapper.wrap(method.getReturnType(), fieldzz.get(this.object));
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to get field.", e);
        }
    }

    private void setField(SetField annotation, Object[] args) {
        args = retrieveParameterObjects(args);
        try {
            Field fieldzz = finder.findField(clazz, annotation.value());
            fieldzz.setAccessible(true);
            fieldzz.set(this.object, args[0]);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to set field.", e);
        }
    }

    private Object invokeMethod(Method method, Object[] args) {
        try {
            Method methodzz = finder.findMethod(clazz, method);
            methodzz.setAccessible(true);
            return wrapper.wrap(method.getReturnType(), methodzz.invoke(this.object, retrieveParameterObjects(args)));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to invoke method", e);
        }
    }

    /**
     * Instantiate the class to mirror if null
     * Look for Class annotation and read value if found
     */
    private void ensureClass() {
        if (clazz == null) {
            Class annotationClass = mirrorDefinition.getAnnotation(Class.class);
            if (annotationClass != null) {
                String clazzName = annotationClass.value();
                try {
                    clazz = finder.findClass(clazzName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new MirrorException("Class not found", e);
                }
            }
        }
    }

    private void ensureObjectClass() {
        if (object != null && !clazz.isInstance(object)) {
            throw new MirrorException("Class doesn't match: instance should be " + clazz + " but is " + object.getClass());
        }
    }

    private void buildAndStoreInstance(Method method, Object[] args) {
        try {
            java.lang.reflect.Constructor<?> constructor = finder.findConstructor(clazz, method);
            constructor.setAccessible(true);
            this.object = constructor.newInstance(retrieveParameterObjects(args));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Can't build object", e);
        }
    }

    private java.lang.Object[] retrieveParameterObjects(Object[] genuineObject) {
        if (genuineObject == null) {
            return genuineObject;
        }
        Object[] objects = new Object[genuineObject.length];
        for (int i = 0; i < objects.length; ++i) {
            objects[i] = unwrapper.unwrap(genuineObject[i]);
        }
        return objects;
    }

    Object getTargetInstance() {
        return this.object;
    }
}
