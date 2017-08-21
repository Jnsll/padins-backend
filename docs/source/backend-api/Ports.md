---
layout: default
id: Ports
title: Backend API
parent: backend-api
---
# Documentation

## `public class Ports extends ArrayList<Port>`

A Ports is a List of Port. See fr.irisa.diverse.Flow.Port to know more about it.

It is used to make inports and outports manipulation easier on the programming side.

Created by antoine on 29/05/17.

## `public void setNode (String node)`

Change the node connected to those Ports

 * **Parameters:** `node` — {String} the id of the node

## `public int indexOfPort (String name)`

Get the index of a given port in this Ports object

 * **Parameters:** `name` — {String} the name of the port
 * **Returns:** {int} the index of the port in the List. Returns -1 if not in Ports.

## `private void build ()`

Build the JSONArray that can be serialized and send to other programs easily.

The Ports JSONArray contains the id of each port in this Ports object.
