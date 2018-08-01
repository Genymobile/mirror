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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Quick helper class to validate Mirror interface definition.
 *
 * Can be greatly improved to report error in a more helpful way.
 */
public class MirrorValidator {

    private ReflectionFinder finder;
    private java.lang.Class<?> mirrorDefinition;

    MirrorValidator(ReflectionFinder finder, java.lang.Class<?> mirrorDefinition) {
        this.finder = finder;
        this.mirrorDefinition = mirrorDefinition;
    }

    public void validate() {
        java.lang.Class<?> targetClass = null;
        try {
            targetClass = validateClass();
        } catch (ClassNotFoundException e) {
            throw new MirrorException("validation failed", e);
        }
        validateMethods(targetClass);
    }

    private void validateMethods(java.lang.Class targetClass) {
        for (Method method : mirrorDefinition.getDeclaredMethods()) {
            validateMethod(targetClass, method);
        }
    }

    private void validateMethod(java.lang.Class targetClass, Method method)  {
        validateMethodAnnotations(method);
        if (method.getAnnotation(Constructor.class) != null) {
            validateConstructor(targetClass, method);
        } else if (method.getAnnotation(GetField.class) != null) {
            validateGetField(targetClass, method);
        } else if (method.getAnnotation(SetField.class) != null) {
            validateSetField(targetClass, method);
        } else if (method.getAnnotation(GetInstance.class) != null) {
            validateGetInstance(method);
        } else if (method.getAnnotation(SetInstance.class) != null) {
            validateSetInstance(method);
        } else {
            validateMethodCall(targetClass, method);
        }
    }

    private void validateGetInstance(Method method) {
        java.lang.Class<?> returnType = method.getReturnType();
        if (!Object.class.equals(returnType)) {
            throw new MirrorException("@GetInstance method must returns an Object instead of " + returnType.getName());
        }
        if (method.getParameterCount() > 0) {
            throw new MirrorException("@GetInstance method must not take any parameters. They will be ignored");
        }
    }

    private void validateSetInstance(Method method) {
        java.lang.Class<?> returnType = method.getReturnType();
        if (!void.class.equals(returnType)) {
            throw new MirrorException("@SetInstance method must returns void instead of " + returnType.getName());
        }
        if (method.getParameterCount() != 1) {
            throw new MirrorException("@SetInstance method must take exactly one parameter");
        }

    }

    private void validateMethodCall(java.lang.Class targetClass, Method method) {
        try {
            Method reflectedMethod = finder.findMethod(targetClass, method);
            validateReturnType(method, reflectedMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            throw new MirrorException("Method not found", e);
        }
    }

    private void validateSetField(java.lang.Class targetClass, Method method) {
        String fieldName = method.getAnnotation(SetField.class).value();
        try {
            finder.findField(targetClass, fieldName);
        } catch (NoSuchFieldException e) {
            throw new MirrorException("Field not found", e);
        }
    }

    private void validateGetField(java.lang.Class targetClass, Method method) {
        String fieldName = method.getAnnotation(GetField.class).value();
        try {
            Field field = finder.findField(targetClass, fieldName);
            validateReturnType(method, field.getType());
        } catch (NoSuchFieldException e) {
            throw new MirrorException("Field not found", e);
        }
    }

    private void validateReturnType(Method mirrorDefinitionMethod, java.lang.Class reflectedReturnType) {
        java.lang.Class<?> mirrorDefinitionReturnType = mirrorDefinitionMethod.getReturnType();
        if (mirrorDefinitionReturnType.isArray() != reflectedReturnType.isArray()) {
            throw new MirrorException(mirrorDefinitionMethod.getName() + " has incorrect return type");
        }

        if (mirrorDefinitionReturnType.isArray()) {
            mirrorDefinitionReturnType = mirrorDefinitionReturnType.getComponentType();
            reflectedReturnType = reflectedReturnType.getComponentType();
        }
        if (mirrorDefinitionReturnType.isPrimitive() && mirrorDefinitionReturnType != reflectedReturnType) {
            throw new MirrorException(mirrorDefinitionMethod.getName() + " has incorrect return type");
        }
        if (mirrorDefinitionReturnType.isPrimitive()) {
            // we are good for a primitive
            return;
        }

        Class classAnnotation = mirrorDefinitionReturnType.getAnnotation(Class.class);
        if (classAnnotation == null) {
            if (mirrorDefinitionReturnType.isAssignableFrom(reflectedReturnType)) {
                // we are good
                return;
            }
            throw new MirrorException(mirrorDefinitionMethod.getName() + " has incorrect return type. "
                    + mirrorDefinitionReturnType.getName() + " doesn't have a @Class annotation");
        }

        if (!classAnnotation.value().equals(reflectedReturnType.getName())) {
            throw new MirrorException(mirrorDefinitionMethod.getName() + " has incorrect return type. "
                    + mirrorDefinitionReturnType.getName() + " doesn't match reflected method return type "
                    + reflectedReturnType.getName());
        }

        Method setInstanceMethod = finder.findSetInstanceMethod(mirrorDefinitionReturnType);
        if (setInstanceMethod == null) {
            throw new MirrorException(
                    "The class " + mirrorDefinitionReturnType.getName() + " has no @SetInstance methods so we cannot wrap it.");
        }
    }

    private java.lang.reflect.Constructor<?> validateConstructor(java.lang.Class targetClass, Method method)  {
        try {
            return finder.findConstructor(targetClass, method);
        } catch (NoSuchMethodException e) {
            throw new MirrorException("Constructor not found", e);
        }
    }

    private void validateMethodAnnotations(Method method) {
        int[] counts = new int[5];
        for (Annotation annotation : method.getAnnotations()) {
            java.lang.Class<? extends Annotation> aClass = annotation.annotationType();
            if (aClass.equals(Constructor.class)) {
                counts[0]++;
            } else if (aClass.equals(GetField.class)) {
                counts[1]++;
            } else if (aClass.equals(SetField.class)) {
                counts[2]++;
            } else if (aClass.equals(GetInstance.class)) {
                counts[3]++;
            } else if (aClass.equals(SetInstance.class)) {
                counts[4]++;
            }
        }
        if (counts[1] > 1) {
            throw new MirrorException("too much GetField");
        }
        if (counts[2] > 1) {
            throw new MirrorException("too much SetField");
        }

        boolean found = false;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0) {
                if (found) {
                    throw new MirrorException("mixing invalid annotations");
                } else {
                    found = true;
                }
            }
        }
    }

    private java.lang.Class<?> validateClass() throws ClassNotFoundException {
        com.genymobile.mirror.annotation.Class classAnnotation = mirrorDefinition.getAnnotation(com.genymobile.mirror.annotation.Class.class);
        if (classAnnotation == null) {
            throw new MirrorException("Your interface must be annotated with @Class");
        }
        String className = classAnnotation.value();
        return finder.findClass(className);
    }
}
