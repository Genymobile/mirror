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

import java.lang.reflect.Proxy;

public final class Mirror {

    public static <T> T create(java.lang.Class<T> clazz) {
        return create(clazz, clazz.getClassLoader());
    }

    public static <T> T create(java.lang.Class<T> clazz, ClassLoader targetClassLoader) {
        Unwrapper unwrapper = new Unwrapper(targetClassLoader);
        ReflectionFinder finder = new ReflectionFinder(targetClassLoader, unwrapper);
        Wrapper wrapper = new Wrapper(targetClassLoader, finder);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new MirrorHandler(clazz, targetClassLoader, wrapper, unwrapper, finder));
    }

    public static void validateMirrorDefinition(java.lang.Class<?> mirrorDefinition) {
        validateMirrorDefinition(mirrorDefinition, mirrorDefinition.getClassLoader());
    }

    public static void validateMirrorDefinition(java.lang.Class<?> mirrorDefinition, ClassLoader targetClassLoader) {
        Unwrapper unwrapper = new Unwrapper(targetClassLoader);
        ReflectionFinder finder = new ReflectionFinder(targetClassLoader, unwrapper);
        MirrorValidator validator = new MirrorValidator(finder, mirrorDefinition);
        validator.validate();
    }

    private Mirror() {
        // don't intantiate
    }
}
