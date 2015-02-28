package com.genymobile.mirror;

import com.genymobile.mirror.annotation.Class;
import com.genymobile.mirror.annotation.Constructor;

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
            buildAndStoreInstance(args);
            return object;
        }

        return null;
    }

    /**
     * Instanciate the class to mirror if null
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
                    //todo throw MirrorExceptio
                }
            }
        }
    }

    private void buildAndStoreInstance(Object[] args) {
        java.lang.Class[] classList = getClasses(args);
        try {
            java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(classList);
            this.object = constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            //todo:
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private java.lang.Class[] getClasses(Object[] objects) {
        java.lang.Class[] classes = new java.lang.Class[objects.length];

        for(int i=0 ; i < objects.length ; i++) {
            classes[i] = objects[i].getClass();
        }

        return classes;
    }
}
