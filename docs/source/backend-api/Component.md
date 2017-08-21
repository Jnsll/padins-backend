---
layout: default
id: Component
title: Backend API
parent: backend-api
---
# Documentation

## `public class Component`

A component is something close to the notion of Class in object-oriented programming. Its instance is a Node.

A component is a concept of Flow-Based Programming and its fields are imposed by the FBP.

There is a library that contains all the components a given workspace can use. This library is in resources/WebUIComponents

Created by antoine on 29/05/17.

## `public String getName ()`

Gives the name of the component

 * **Returns:** name as String

## `public String getDescription()`

Gives the description of a Component. Usually what it's made for.

 * **Returns:** the description as String

## `Ports getInports()`

Gives the list of inports the Component has. The inports are ports to connect to when on the graph.

 * **Returns:** the list of inports

## `Ports getOutports()`

Gives the list of outports the Component has. The outports are ports to send data to an other node's inport.

 * **Returns:** the list of outports

## `public String getLanguage()`

Gives the programming language used by the component

 * **Returns:** the programming language as String

## `public String getCode()`

Gives the source code of the component

 * **Returns:** the source code as String

## `public String getTests()`

Gives the tests code of the component

 * **Returns:** the tests code as String

## `public boolean isExecutable()`

Whether the component is executable or not

 * **Returns:** true if executable

## `private void buildJson ()`

Build the JSON object component with up-to-date information. Usually used right before serializing the message.

## `@Override public String toString ()`

Override the very common toString function that return a String representing the object.

 * **Returns:** the component serialized

## `public JSONObject toJson()`

Give the JSON containing all the information about the component

 * **Returns:** the JSON as JSONObject
