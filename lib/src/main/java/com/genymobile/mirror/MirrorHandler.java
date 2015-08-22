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

public class MirrorHandler<T> implements InvocationHandler {

    private final java.lang.Class<T> proxyClass; //interface created by user
    private final Wrapper wrapper;
    private final Unwrapper unwrapper;

    private java.lang.Class<?> clazz; //class to mirror
    private Object object; //target object

    public MirrorHandler(java.lang.Class<T> proxyClass, Wrapper wrapper, Unwrapper unwrapper) {
        this.proxyClass = proxyClass;
        this.wrapper = wrapper;
        this.unwrapper = unwrapper;
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
            return getField(getField, method);
        }

        //no annotation found, try to call method
        return invokeMethod(method, args);
    }

    private Object getField(GetField getField, Method method) {
        try {
            Field fieldzz = clazz.getDeclaredField(getField.value());
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
            Method methodzz = clazz.getDeclaredMethod(method.getName(), retrieveParameters(method));
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
        java.lang.Class[] classList = retrieveParameters(method);
        try {
            java.lang.reflect.Constructor<?> constructor = clazz.getDeclaredConstructor(classList);
            constructor.setAccessible(true);
            this.object = constructor.newInstance(retrieveParameterObjects(args));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Can't build object", e);
        }
    }

    private java.lang.Class[] retrieveParameters(Method method) {
        java.lang.Class[] genuineTypes = method.getParameterTypes();
        java.lang.Class[] types = new java.lang.Class[genuineTypes.length];

        for (int i = 0; i < genuineTypes.length; ++i) {
            types[i] = unwrapper.unwrapClass(genuineTypes[i]);
        }
        return types;
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
