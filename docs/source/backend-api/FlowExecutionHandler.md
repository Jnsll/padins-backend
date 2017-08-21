---
layout: default
id: FlowExecutionHandler
title: Backend API
parent: backend-api
---
# Documentation

## `public class FlowExecutionHandler`

Handle the execution of one group or flow. A group or flow is composed of several nodes. One node is one block on the UI.

You have to create one instance of this class per flow you want to run. Then, when you want to run a flow, it works has following : 1 - Search for the first Nodes to execute (the ones with no inputs connected) 2 - Run these first nodes. To do that, we put all the nodes to execute in a Set. Beside that, we have a master that look at each node in the Set and run it if possible. The execution of a node is done in a new thread. After starting the execution of a node, it put the thread into a Running Set. When the execution of a Node in a thread finishes, it adds the nodes following the one that just runned into the toLaunch Set. In order to run a node, the master verify that its dependencies have finished their own execution. If not, it continues going through the Set, looking for nodes that can be launched. When both Set (toLaunch and running) are empty, we stop the master and the execution of the flow is finished.

Created by antoine on 02/06/17.

## `public void run ()`

Start the execution of the flow given to the constructor

## `public void stop ()`

Stop the flow's execution

## `synchronized public void addToLaunch (Node n)`

Add a node to the list of nodes that will be started as soon as possible

 * **Parameters:** `n` — : the Node to add

## `public void runningThreadFinished (Thread t)`

Method for the Thread to prevent that it finished. It will remove it from the list of running nodes (1 node <-> 1 thread).

 * **Parameters:** `t` — : the Thread that finished.

## `public boolean isRunning ()`

 * **Returns:** a boolean telling whether the Execution of the Flow is running or not

## `private void runNodes ()`

Starts the flow to execute.

It begins with retrieving the first nodes to execute. Then put them into the toLaunch set and start doing the master job, as described above.

## `private void stopNodes (ArrayList<Node> nodes)`

Stop the execution of the given nodes

 * **Parameters:** `nodes` — : The List of nodes to stop

## `public void errorExecutingNode (Node n)`

Handle the execution of the workflow when an error occurs during the execution of the given node.

 * **Parameters:** `n` — {Node} the node that thrown an error

## `private void removeThread (Thread t)`

Remove a given thread from everywhere it is stored in the class.

 * **Parameters:** `t` — : the Thread to remove

## `private void prepareNodesForExecution ()`

Prepare the nodes for being executed.

## `private void runNode (Node node)`

Run a unique node. It starts a new Thread for the node and add it to the Running set.

 * **Parameters:** `node` — : the Node to execute

## `private boolean havePreviousNodesFinish(Node n)`

Tells whether the dependency of a node finished running. This method is usually called in order to know if it is possible de run a node.

 * **Parameters:** `n` — : the Node for which you want to know if dependencies finished running.
 * **Returns:** True if all the dependencies finished
