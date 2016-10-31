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

public final class Sample {

    private Sample() {
        // don't intantiate
    }

    public static void main(String[] args) {

        System.out.println("Sample Application");

        System.out.println("Create wrapper");
        ShyClassWrapper shyClass = Mirror.create(ShyClassWrapper.class);

        System.out.println("Build ShyClass");
        shyClass.buildShyClass(18);

        String value;

        value = shyClass.privateFieldToString();
        System.out.println("Print privateField via public method: " + value);

        System.out.println("Set private field");
        shyClass.setPrivateField(10);

        value = shyClass.privateFieldToString();
        System.out.println("Print privateField via public method: " + value);

        value = shyClass.sumUp(32);
        System.out.println("Call private method: " + value);
    }
}
