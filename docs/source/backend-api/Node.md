---
layout: default
id: Node
title: Backend API
parent: backend-api
---
# Documentation

## `public class Node implements Comparable<Node>`

A node correspond to a block on a flow-based program A node is a part of a flow.

To know more about Flow-Based programming : http://www.jpaulmorrison.com/fbp/concepts.html To see the FBP Network Protocol : https://flowbased.github.io/fbp-protocol/#graph-addnode

Created by antoine on 29/05/17.

## `public String getId ()`

Get the unique id of the node.

 * **Returns:** {String} the uuid of the node

## `public void setId (String newId)`

Update the id of the node. The ID must be unique !

 * **Parameters:** `newId` — {String} the new uuid of the node

## `public Ports getInports()`

Get the inports of the node.

 * **Returns:** {Ports} the inports of the node

## `public Ports getOutports()`

Get the outports of the node.

 * **Returns:** {Ports} te outports of the node

## `public JSONObject getMetadata()`

Get the metadata of the node.

 * **Returns:** {JSONObject} the metadata of the node

## `public void setMetadata(JSONObject metadata)`

Set the metadata of the node

 * **Parameters:** `metadata` — {JSONObject} the new metadata. Must contain all the metadata, not only the new ones

## `public String getGraph()`

Get the id of the graph the node is into. The graph can be the root Flow or a group.

 * **Returns:** {String} the id of the graph

## `public String getComponent()`

Get the component on which this node is based. The component name is formatted as : {{Library}}/{{ComponentName}}

 * **Returns:** {String} the name of the component with its library name

## `public JSONObject getJson()`

Get the JSON object containing all the data of the node. Usually used to send the node data to another service.

 * **Returns:** {JSONObject} the node's json object

## `public boolean isExecutable ()`

Is the node executable ?

 * **Returns:** {boolean} true if executable, no elsewere

## `public String getCode ()`

Get the python code of the node. Will be null if not executable.

 * **Returns:** {String} the code of the node. Only python support for now.

## `public JSONObject getResult ()`

Get the result of the node's execution if executable, pickle formatted. Otherwise all the data that it contains. Usually used to inject the data in the beginning of the next nodes for its execution.

 * **Returns:** {JSONObject} containing all the data with a key: value format

## `public JSONObject getJsonResult()`

Get the data of the node as a JSON formatted as key: value pairs.

 * **Returns:** {JSONObject} containing data as key: value

## `public JSONObject getPickledResult()`

Get the data of the node as pairs of key: pickle. A pickle is a string that is the result of the serialization of any variable in python using pickle.dumps. -> See python documentation.

 * **Returns:** {JSONObject} key: pickle pairs

## `public void setPickledResult (JSONObject result)`

Set the pickle result object. A pickle is a string that is the result of the serialization of any variable in python using pickle.dumps. -> See python documentation.

 * **Parameters:** `result` — {JSONObject} Must be formatted as key: pickle pairs

## `public void setJsonResult(JSONObject result)`

Set the result object, containing all the data to transfer to the nodes connected to the outports.

 * **Parameters:** `result` — {JSONObject} Must be formatted as key: value pairs with value being a stringify json

## `public void assignPortToEdge (String port, String edge)`

Store the information that an edge has been connected to a port of the node.

 * **Parameters:**
   * `port` — {String} the name of the port on which the edge has been connected
   * `edge` — {String} the id of the connected edge

## `public void unassignPortToEdge (String port, String edge)`

Remove the association between the port of the node and the edge

 * **Parameters:**
   * `port` — {String} the name of the port on which the edge has been disconnected
   * `edge` — {String} the id of the disconnected edge

## `public JSONObject getPreviousNodesData()`

Retrieve the data of all the ports connected to the inports of this node.

 * **Returns:** {JSONObject} containing the data (variables ) as key:pickle pairs and the key:stringified-json pairs

## `public ArrayList<Node> previousInFlow ()`

Retrieve the list of nodes connected to the inports.

 * **Returns:** {ArrayList<Node>} the list of node connected to the inports

## `public ArrayList<Node> nextInFlow ()`

Retrieve the list of nodes connected to the outports. Usually used in order to know what are the next nodes to execute when running the flow.

 * **Returns:** {ArrayList<Node>} the list of node connected to the outports

## `public boolean isRunning ()`

Is the node running ?

 * **Returns:** {boolean} True if running, False if not

## `public boolean hasFinished ()`

Has the node finished running ?

 * **Returns:** {boolean} True if run finished, False if still running

## `public boolean shouldBeReRun ()`

Should the node be re-run ? If this node and this node's previous nodes in the flow haven't been modified since the last run it is not necessary to run it again, the result will be exactly the same.

To make sure this is reliable, it will verify that all nodes connected to the inports of this node haven't been modified, and their own previous node that will themselves verified that their previous node haven't been modified and so on, recursively.

 * **Returns:** 

## `public boolean noKnownError ()`

Has this node a know error ? We know that the node as an error if an error has been thrown during this node's last run. Then we check if the user modified his/her code since the last error. If he/she does, we suppose that he/she corrected the error.

 * **Returns:** {boolean} True if we are sure that there is an error in the code.

## `public boolean lastRunReturnedError ()`

Has the last run returned an error ?

 * **Returns:** {boolean} True if the last run returned an error, false otherwise

## `public void prepareForExecution ()`

Prepare the node for the execution, stopping it if it is already running.

## `public boolean receivedResultAfterTime (long time)`

Has the node received a result after the given time ?

 * **Parameters:** `time` — {long} the timestamp after which you want to know if the node received a result
 * **Returns:** {boolean} true if the node received the result of its execution after the given time.

## `public void errorOccurred ()`

Method used to prevent this node that an error occurred during its execution.

## `public void emptyTraceback ()`

Empty the traceback in the metadata

## `private void build ()`

Build the json object representing the node. The json is used to send all the information of this group to any other service.

The JSONObject contains : - id {String} - component {String} - metadata {JSONObject} - graph {String} - inports {JSONObject} - outports {JSONObject}

## `private Port findPort (String name)`

Get a Port from its name

 * **Parameters:** `name` — {String} the name of the port, no matter if it's an inport or outport
 * **Returns:** {Port} the port with the given name, null if not found

## `private Port findPortInGivenObject (Ports ports, String name)`

Get a Port with the given name in the given Ports object

 * **Parameters:**
   * `ports` — {Ports} the given ports where to search for the port with the given name
   * `name` — {String} the name of the searched port
 * **Returns:** {Port} the port found, null if not found

## `private ArrayList<Node> nextOrPreviousNodeInFlow (Ports ports)`

Give all the nodes connected to the opposite side of the edges connected to the given ports. The given ports must be either the inports of this block or the ouports.

 * **Parameters:** `ports` — {Ports} the ports you want to get the nodes connected to the opposite of the edges
 * **Returns:** {ArrayList<Node>} the list of nodes connected to the opposite of the edges connected to the given ports

## `private ArrayList<Node> oppositeNodesForPort(Port p, boolean previousNode)`

Get the nodes connected to the opposite side of the edges connected to the given port. Choose between getting the nodes at the beginning or at the end of the edges.

 * **Parameters:**
   * `p` — {Port} the port used to retrieve the connected edges and their src or tgt nodes
   * `previousNode` — {boolean} do you want to previous nodes or next nodes ?

     Using the flow execution order as the reference for previous or next.
 * **Returns:** {ArrayList<Node>} the list of nodes on the opposite of the port

## `private void sendUpdateNodeMessage ()`

Send an updatenode message to the UIs connected to the workspace this node is on.

## `private void nodeUpdated ()`

React to a nodeUpdated event. The reaction is simply storing the timestamp of the last modification of this node.
