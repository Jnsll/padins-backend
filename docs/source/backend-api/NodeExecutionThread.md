---
layout: default
id: NodeExecutionThread
title: Backend API
parent: backend-api
---
# Documentation

## `public class NodeExecutionThread extends Thread implements Comparable<NodeExecutionThread>`

Thread that manage the execution of a node via the Jupyter kernel.

Created by antoine on 02/06/2017.

## `@Override public void run()`

The run method is the main method of a Thread. It verify that the node should be re-runned, if so launch it. If not, directly go to next step. The next step is adding the following nodes in the toLaunch set and removing this thread from the list of running thread in the FlowExecutionHandler and

## `@Override public int compareTo(NodeExecutionThread o)`

CompareTo method from Comparable interface. It is used by an ordered Set to determine where to add an instance of this class.

 * **Parameters:** `o` — : the object to compare to
 * **Returns:** -1 if this is inferior to o, 0 if equal, 1 if greater
