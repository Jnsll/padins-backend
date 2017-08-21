---
layout: default
id: ComponentsUtils
title: Backend API
parent: backend-api
---
# Documentation

## `public class ComponentsUtils`

A Utils class to make easier retrieving components and their inports and outports.

Created by antoine on 29/05/17.

## `public static Ports getInPortsForComponent(String library, String component, String node)`

Give all the inports given the name of a component

 * **Parameters:** `component` — : the component for whom you want its inports
 * **Returns:** : An arraylist containing all inports

## `public static Ports getOutPortsForComponent(String library, String component, String node)`

Give all the outports given the name of a component

 * **Parameters:** `component` — : the component for whom you want its inports
 * **Returns:** : An arraylist containing all inports

## `public static ArrayList<Component> getComponentsFromLib (String library)`

Retrieve all the components for a given library

 * **Parameters:** `library` — The library of components
 * **Returns:** The list of components

## `public static Component getComponent(String library, String name)`

Retrieve a specific component for a given name and library

 * **Parameters:**
   * `library` — The library of components
   * `name` — The search component's name
 * **Returns:** The component requested if existing. Otherwise null.
