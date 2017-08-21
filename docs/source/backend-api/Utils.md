---
layout: default
id: Utils
title: Backend API
parent: backend-api
---
# Documentation

## `public class Utils`

A Utils class to store methods that can be useful anywhere across the code, without the need to instantiate it.

Created by antoine on 02/06/17.

## `public static Status getGraphStatus (Object graph)`

Returns the status of the given graph.

 * **Parameters:** `graph` — {Flow or Group} the graph to get the status for.
 * **Returns:** {Status} the status of the graph

## `public static void wait (int millis)`

Wait N milliseconds, N being the input.

 * **Parameters:** `millis` — {int} the time to wait, in ms

## `public static String StringArrayToString (String[] input)`

Transform an array of String into a single String.

 * **Parameters:** `input` — {String[]} the input the transform
 * **Returns:** {String} the single line String
