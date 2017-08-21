---
layout: default
id: Port
title: Backend API
parent: backend-api
---
# Documentation

## `public class Port`

A Port is either an input or output of a Node. It offers the possibility to connect nodes together.

For a functional purpose, a port has a name and a port. The name is the label that should be displayed, and the port correspond to the name used to differentiate two ports.

We use several inports or outports to offer different connexion functionality. For example, a node Addition could have to inports A and B and one outport sum. A and B are used to differentiate the two data to sum, and the outport is used to transmit the result to another node. We can link it to another sum node and sum the result with another B data. An so on.

Created by antoine on 29/05/17.

## `public String getId()`

Get the id of the Port

 * **Returns:** {String} the id of the port

## `public String getPort ()`

Get the port name, the one used by the program to differentiate ports.

 * **Returns:** {String} the port's name

## `public JSONObject getMetadata ()`

Get the metadata of the port.

 * **Returns:** {JSONObject} the metadata

## `public ArrayList<String> getConnectedEdgesId()`

Get the connected edges'id of the port.

This method can be used to make easier retrieving the nodes connected to the opposite of the edge connected to this port.

Several edges can be connected on one port.

 * **Returns:** {ArrayList<String>} the list of the ids of the edges connected to the port.

## `public String getType()`

Get the type of data that can be connected to this port.

 * **Returns:** 

## `public String getName()`

Get the public name of the port, the one to display on the UIs.

 * **Returns:** {String} the name of the port.

## `public String getNode()`

Get the node on which this port is.

 * **Returns:** {Node} the node containing the port.

## `public void setNode(String node)`

Set the node containing this port.

 * **Parameters:** `node` — {Node} the new node containing this port.

## `public void setConnectedEdges(ArrayList<String> connectedEdges)`

Set the list of edges connected to this port.

 * **Parameters:** `connectedEdges` — {ArrayList<String>} the list of the ids of the edges connected to this port.

## `public void addConnectedEdge (String edgeId)`

Connect a new edge to this port.

 * **Parameters:** `edgeId` — {String} the id of the edge.

## `public void removeConnectedEdge (String edgeId)`

Disconnect an edge from this port

 * **Parameters:** `edgeId` — {String} the id of the edge

## `private void build ()`

Build the port object in order to create a JSON that can be serialized and sent.

The Port object JSONObject contains : - id {String} - public {String} - node {String} - port {String} - metadata {JSONObject} - connectedEdges {List<String>} - type {String}
