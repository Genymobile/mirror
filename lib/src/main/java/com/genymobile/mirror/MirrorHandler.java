package com.genymobile.mirror;

import com.genymobile.mirror.annotation.*;
import com.genymobile.mirror.annotation.Class;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MirrorHandler<T> implements InvocationHandler {

    private java.lang.Class<?> clazz; //class to mirror
    private java.lang.Class<T> proxyClass; //interface created by user

    private Object mirroredObject; //target object

    public MirrorHandler(java.lang.Class<T> proxyClass) {
        this.proxyClass = proxyClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ensureClass(proxy);

        if (method.getAnnotation(Constructor.class) != null) {
            buildAndStoreInstance(proxy, args);
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

    private void buildAndStoreInstance(Object proxy, Object[] args) {
    }
}
