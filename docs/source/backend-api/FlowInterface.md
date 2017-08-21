---
layout: default
id: FlowInterface
title: Backend API
parent: backend-api
---
# Documentation

## `interface FlowInterface`

Interface describing the methods a Flow must have.

Most of the methods corresponds to messages exchanged on the Flow Based Programming Network Protocol.

Created by antoine on 30/05/17.

## `boolean addNode (String id, String component, JSONObject metadata, String graph, boolean executable)`

Add a node to the flow

 * **Parameters:**
   * `id` — The id of the node
   * `component` — The component of the node
   * `metadata` — The metadata object of the node
   * `graph` — The graph in which to add the node
   * `executable` — Whether the node is executable or not
 * **Returns:** True if the node has been added

## `boolean removeNode (String id, String graph)`

Remove a node from the flow

 * **Parameters:**
   * `id` — The id of the node to remove
   * `graph` — The graph from which to remove the node
 * **Returns:** 

## `boolean renameNode (String from, String to, String graph)`

Change the id of a node

 * **Parameters:**
   * `from` — : the previous id
   * `to` — : the new id
   * `graph` — : the graph where the node is
 * **Returns:** True if successfully done

## `boolean changeNode (String id, JSONObject metadata, String graph)`

Update the metadata of a node.

 * **Parameters:**
   * `id` — the id of the node
   * `metadata` — the new metadata
   * `graph` — the graph where the node is
 * **Returns:** True if successfully done

## `boolean addEdge (String id, JSONObject src, JSONObject tgt, JSONObject metadata, String graph)`

Add an edge, connecting two nodes on the graph.

 * **Parameters:**
   * `id` — the id of the new edge
   * `src` — the src node of the edge
   * `tgt` — the tgt node of the edge
   * `metadata` — the metadata of the edge
   * `graph` — the graph where the edge is
 * **Returns:** True if successfully added and connected.

## `boolean removeEdge (String id, String graph, JSONObject src, JSONObject tgt)`

Remove an existing edge from the graph.

 * **Parameters:**
   * `id` — the id of the edge
   * `graph` — the graph where the edge is
   * `src` — the src node of the edge
   * `tgt` — the tgt node of the edge
 * **Returns:** True if successfully removed

## `boolean changeEdge (String id, String graph, JSONObject metadata, JSONObject src, JSONObject tgt)`

Modify the src, tgt and metadata of an edge

 * **Parameters:**
   * `id` — the unique id of the edge
   * `graph` — the graph where the edge is
   * `metadata` — the new metadata of the edge
   * `src` — the new src object of the edge
   * `tgt` — the tgt object of the edge
 * **Returns:** True if the modification has successfully be done

## `boolean addGroup (String name, JSONArray nodes, JSONObject metadata, String graph)`

Create a new group that is a kind of subgraph user can run independently.

 * **Parameters:**
   * `name` — the name of the new group
   * `nodes` — the nodes in this group
   * `metadata` — the metadata of the new group
   * `graph` — the graph where the group is (can be another group)
 * **Returns:** True if successfully created

## `boolean removeGroup (String name, String graph)`

Remove an existing group

 * **Parameters:**
   * `name` — name of the group to delete
   * `graph` — the graph where the group is
 * **Returns:** True if successfully deleted

## `boolean renameGroup (String from, String to, String graph)`

Rename a group.

 * **Parameters:**
   * `from` — the old name
   * `to` — the new name
   * `graph` — the graph where the group is
 * **Returns:** True if successfully changed

## `boolean changeGroup (String name, JSONObject metadata, String graph)`

Change the metadata of a group.

 * **Parameters:**
   * `name` — name of the group
   * `metadata` — new metadata of the group
   * `graph` — the graph where the group is
 * **Returns:** True if successfully changed

## `String serialize ()`

Serialize the flow as json

 * **Returns:** the serialized flow
