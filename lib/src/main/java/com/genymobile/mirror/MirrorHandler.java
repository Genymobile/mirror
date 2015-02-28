package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;
import com.genymobile.mirror.annotation.SetField;
import com.genymobile.mirror.annotation.SetInstance;
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

        ensureClass(proxy);

        if (method.getAnnotation(Constructor.class) != null) {
            buildAndStoreInstance(method, args);
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

        //no annotation found, try to call method
        return invokeHiddenMethod(method, args);
    }

    private void setField(SetField annotation, Object[] args) {
        try {
            Field fieldzz = clazz.getDeclaredField(annotation.value());
            fieldzz.setAccessible(true);
            fieldzz.set(this.object, args[0]); // todo ...
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to access field.", e);
        }
    }

    private Object invokeHiddenMethod(Method method, Object[] args) {
        try {
            Method methodzz = clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
            methodzz.setAccessible(true);
            return methodzz.invoke(this.object, args);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Error while trying to invoke method", e);
        }
    }

    /**
     * Instantiate the class to mirror if null
     * Look for Class annotation and read value if found
     *
     * @param proxy
     */
    private void ensureClass(Object proxy) {
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
        if (object.getClass() != clazz) {
            throw new MirrorException("Class doesn't match", new Throwable());
        }
    }

    private void buildAndStoreInstance(Method method, Object[] args) {
        java.lang.Class[] classList = method.getParameterTypes();
        try {
            java.lang.reflect.Constructor<?> constructor = clazz.getDeclaredConstructor(classList);
            constructor.setAccessible(true);
            this.object = constructor.newInstance(args);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MirrorException("Can't build object", e);
        }
    }
}
