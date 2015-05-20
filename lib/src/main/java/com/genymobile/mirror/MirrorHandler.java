package com.genymobile.mirror;

import com.genymobile.mirror.annotation.*;
import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;
import com.genymobile.mirror.annotation.SetInstance;
import com.genymobile.mirror.exception.MirrorDeveloperException;
import com.genymobile.mirror.exception.MirrorException;

import java.lang.reflect.*;

public class MirrorHandler<T> implements InvocationHandler {

    private java.lang.Class<?> clazz; //class to mirror
    private java.lang.Class<T> proxyClass; //interface created by user

    private Object object; //target object

    public MirrorHandler(java.lang.Class<T> proxyClass) {
        this.proxyClass = proxyClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

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
                throw new MirrorException("Missing object", new Throwable());
            }
        }

        SetField setField = method.getAnnotation(SetField.class);
        if (setField != null) {
            setField(setField, args);
            return null;
        }

        GetField getField = method.getAnnotation(GetField.class);
        if (getField != null) {
            return getField(getField);
        }

        //no annotation found, try to call method
        return invokeMethod(method, args);
    }

    private Object getField(GetField getField) {
        try {
            Field fieldzz = clazz.getDeclaredField(getField.value());
            fieldzz.setAccessible(true);
            return fieldzz.get(this.object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to get field.", e);
        }
    }

    private void setField(SetField annotation, Object[] args) {
        args = retrieveParameterObjects(args);
        try {
            Field fieldzz = clazz.getDeclaredField(annotation.value());
            fieldzz.setAccessible(true);
            fieldzz.set(this.object, args[0]);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to set field.", e);
        }
    }

    private Object invokeMethod(Method method, Object[] args) {
        try {
            Method methodzz = clazz.getDeclaredMethod(method.getName(), retrieveParameterTypes(method));
            methodzz.setAccessible(true);
            return wrapResult(method.getReturnType(), methodzz.invoke(this.object, retrieveParameterObjects(args)));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to invoke method", e);
        }
    }

    /**
     * Instantiate the class to mirror if null
     * Look for Class annotation and read value if found
     *
     */
    private void ensureClass() {
        if (clazz ==  null) {
            Class annotationClass = proxyClass.getAnnotation(Class.class);
            if (annotationClass != null ) {
                String clazzName = annotationClass.value();
                try {
                    clazz = java.lang.Class.forName(clazzName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new MirrorException("Class not found", e);
                }
            }
        }
    }

    private void ensureObjectClass() {
        if (object != null && object.getClass() != clazz) {
            throw new MirrorException("Class doesn't match", new Throwable());
        }
    }

    private void buildAndStoreInstance(Method method, Object[] args) {
        java.lang.Class[] classList = retrieveParameterTypes(method);
        try {
            java.lang.reflect.Constructor<?> constructor = clazz.getDeclaredConstructor(classList);
            constructor.setAccessible(true);
            this.object = constructor.newInstance(retrieveParameterObjects(args));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Can't build object", e);
        }
    }

    private java.lang.Class[] retrieveParameterTypes(Method method) {
        java.lang.Class[] genuineTypes = method.getParameterTypes();
        java.lang.Class[] types = new java.lang.Class[genuineTypes.length];

        for (int i = 0; i < genuineTypes.length; ++i) {
            types[i] = genuineTypes[i];
            Class annotation = (Class) types[i].getAnnotation(Class.class);
            if (annotation != null) {
                try {
                    types[i] = java.lang.Class.forName(annotation.value());
                } catch (ClassNotFoundException e) {
                    throw new MirrorException("Cannot find class for this type.", e);
                }
            }
        }
        return types;
    }

    private java.lang.Object[] retrieveParameterObjects(Object[] genuineObject) {
        if (genuineObject == null) {
            return genuineObject;
        }
        Object[] objects = new Object[genuineObject.length];
        for (int i = 0; i < objects.length; ++i) {
            objects[i] = genuineObject[i];
            if (Proxy.isProxyClass(objects[i].getClass())) {
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(objects[i]);
                if (invocationHandler instanceof MirrorHandler) {
                    objects[i] = ((MirrorHandler) invocationHandler).getInstance();
                }
            }
        }
        return objects;
    }

    private Object getInstance() {
        return this.object;
    }

    private Object wrapResult(java.lang.Class clazz, Object result) throws InvocationTargetException, IllegalAccessException {
        if (isWrapperClass(clazz)) {
            Object object = Mirror.create(clazz);
            Method setInstance = findSetInstanceMethod(clazz);
            setInstance.invoke(object, result);
            return object;
        } else {
            return result;
        }

    }

    private boolean isWrapperClass(java.lang.Class clazz) {
        return clazz.getAnnotation(Class.class) != null;
    }

    private Method findSetInstanceMethod(java.lang.Class clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(SetInstance.class) != null) {
                return method;
            }
        }
        throw new MirrorDeveloperException("The class " + clazz.getName() + " has no setInstance() methods so we cannot wrap any result.");
    }
}
