---
layout: default
id: JSON
title: Backend API
parent: backend-api
---
# Documentation

## `public abstract class JSON`

Utils class that implements a few useful functions to make JSON manipulation easier.

Created by antoine on 26/05/2017.

## `public static JSONObject stringToJsonObject (String s)`

Parse and return a JSONObject from a String

 * **Parameters:** `s` — : the string to parse
 * **Returns:** : a JSONObject that can be manipulate

## `public static String jsonArrayListToString (ArrayList list)`

Transform an array list into a JSON formatted String.

 * **Parameters:** `list` — {ArrayList} the list to format
 * **Returns:** {String} a JSON formatted array

## `public static String[] jsonObjectToStringArray (JSONObject obj)`

Transform a JSON object into an array of string.

 * **Parameters:** `obj` — {JSONObject} the object to parse
 * **Returns:** {String[]} an array of string with the given object content

## `public static ArrayList<String> jsonObjectToArrayList (JSONObject obj)`

Transform a JSONObject into an ArrayList<String>

 * **Parameters:** `obj` — {JSONObject} the object to transform
 * **Returns:** {ArrayList<String>} containing everything from the given object

## `public static JSONArray jsonArrayFromArrayList (ArrayList list)`

Transform an ArrayList into a JSONArray.

 * **Parameters:** `list` — {ArrayList} the list to parse
 * **Returns:** {JSONArray} the list formatted as a JSON array
