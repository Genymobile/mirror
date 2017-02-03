# ![Mirror](icon_mirror.png)Mirror

_Easy reflection for Java and Android_

## Introduction

_Mirror_ turns a private class you want to use reflection on into a Java interface.
```java
@Class("com.framework.PrivateCoolClass")
public interface CoolClass {
    @Constructor
    void callConstructor(String aString);

    // A cool method, we want to call
    String doCoolStuff(int value);
}
```

The ```Mirror``` class generates an implementation of the ```CoolClass``` interface.
```java
CoolClass coolClass = Mirror.create(CoolClass.class)
```

You'll need to set up the wrapper with an instance first, for example by calling a constructor.
```java
coolClass.callConstructor("Super"); // this call the PrivateCoolClass(String) constructor;
```

Each call on the generated ```CoolClass``` wrapper makes a call on the instance of ```PrivateCoolClass```.

```java
// This will call the method doCoolStuff(int) on the PrivateCoolClass Object
String cool = coolClass.doCoolStuff(42);
```

## Refection annotations

Use _Mirror_'s annotations to describe your object wrapper and how you want to interact with it.

### Declaring the private Class

Use the ```Class``` annotation to setup the class you want to use reflection on.
```java
@Class("com.framework.private.PrivateClass")
public interface NotSoPrivateClass {
  ...
}
```

### Instance management

Each wrapper needs to be fed with an instance before any other call.
```java
@Constructor
void callConstructor(String aString);
// or
@SetInstance
void setInstance(Object object);
```

There are two way for setting an instance into a wrapper:
  - Calling a ```Constructor``` annotated method that will create the instance using the constructor matching the exact same parameters.
  - Calling a ```SetInstance``` annotated method that will use this object as the instance.

If you need to retrieve the instance, simply add a method annotated with ```GetInstance``` returning an object.
```java
@GetInstance
Object getInstance();
```

### Calling methods

If not annotated, a method will be directly called on the instance using the exact same signature.

### Playing with fields

```SetField``` and ```GetField``` can be used to set and get fields from the instance. Both annotation needs the name of the field to work.
```java
@GetField("aField")
Object getField();

@SetField("aField")
void setField(String aString);
```

## Download

Gradle:

    compile 'com.genymobile:mirror:1.0.0'

Mirror requires at minimum Java 7.

## Cool but wait... Warning!

This is super slow. Everything is done at runtime (internal reflexion, wrapping...) so don't use this for intensive work.
The main goal of this library is to give a tool to quickly play with reflexion and private APIs, classes... and software prototyping.

## What next?

There is still some refactoring to do, plenty of bugs to fix, safety checks to implements. Maybe a v2 with code generation to get rid of the performance issue.
Anyway, do not hesitate to file bugs or contribute.

# License
```
Copyright 2016 Genymobile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
