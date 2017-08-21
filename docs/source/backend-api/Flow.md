---
layout: default
id: Flow
title: Backend API
parent: backend-api
---
# Documentation

## `public class Flow implements FlowInterface`

The Flow is the main data structure of the project.

It contains the graph, that is the ensemble of elements that describe the process that the user wants to execute/simulate. This process is composed of nodes, connected with edges.

The flow also contains groups, that are some subgraph of the graph. They are used to let the user simulate some part of the graph instead of everything.

Beside that, the flow contains the library of components available.

We represent and store the flow as a JSON file. The web interface uses it, and only it, to create the view.

Created by antoine on 26/05/2017.

## `public Flow (JSONObject source, Workspace workspace)`

Use case : after server restart, re-create workspaces' flows from the saved JSON files.

 * **Parameters:** `source` — : the parsed file

## `public String serialize ()`

Serialize the Flow as a JSON and return it

 * **Returns:** a JSON representation of the flow

## `private void buildObject()`

Build the Json object with the following structure

{ 'id': string, 'name': string, 'library': string, 'description': string, 'edges': Edge[], 'nodes': Node[], 'groups': Group[] }

## `public boolean addNode(String id, String component, JSONObject metadata, String graph, boolean executable)`

Add a new node onto the graph

 * **Parameters:**
   * `id` — The id of the node
   * `component` — The component of the node
   * `metadata` — The metadata object of the node
   * `graph` — The graph in which to add the node
   * `executable` — Whether the node is executable or not
 * **Returns:** True if added

## `public boolean removeNode(String id, String graph)`

Remove an existing node from the graph.

 * **Parameters:**
   * `id` — The id of the node to remove
   * `graph` — The graph from which to remove the node
 * **Returns:** True if removed

## `public boolean renameNode(String from, String to, String graph)`

Change the id of a node

 * **Parameters:**
   * `from` — : the previous id
   * `to` — : the new id
   * `graph` — : the graph where the node is
 * **Returns:** True if successfully done

## `public boolean changeNode(String id, JSONObject metadata, String graph)`

Update the metadata of a node.

 * **Parameters:**
   * `id` — the id of the node
   * `metadata` — the new metadata
   * `graph` — the graph where the node is
 * **Returns:** True if successfully done

## `public boolean addEdge (String id, JSONObject src, JSONObject tgt, JSONObject metadata, String graph)`

Add an edge, connecting two nodes on the graph.

 * **Parameters:**
   * `id` — the id of the new edge
   * `src` — the src node of the edge
   * `tgt` — the tgt node of the edge
   * `metadata` — the metadata of the edge
   * `graph` — the graph where the edge is
 * **Returns:** True if added

## `public boolean removeEdge(String id, String graph, JSONObject src, JSONObject tgt)`

Remove an existing edge from the graph.

 * **Parameters:**
   * `id` — the id of the edge
   * `graph` — the graph where the edge is
   * `src` — the src node of the edge
   * `tgt` — the tgt node of the edge
 * **Returns:** True if removed

## `public boolean changeEdge(String id, String graph, JSONObject metadata, JSONObject src, JSONObject tgt)`

Modify the src, tgt and metadata of an edge

 * **Parameters:**
   * `id` — the unique id of the edge
   * `graph` — the graph where the edge is
   * `metadata` — the new metadata of the edge
   * `src` — the new src object of the edge
   * `tgt` — the tgt object of the edge
 * **Returns:** True if changed

## `public boolean addGroup(String name, JSONArray nodes, JSONObject metadata, String graph)`

Create a new group that is a kind of subgraph user can run independently.

 * **Parameters:**
   * `name` — the name of the new group
   * `nodes` — the nodes in this group
   * `metadata` — the metadata of the new group
   * `graph` — the graph where the group is (can be another group)
 * **Returns:** 

## `public boolean removeGroup(String name, String graph)`

Remove an existing group

 * **Parameters:**
   * `name` — name of the group to delete
   * `graph` — the graph where the group is
 * **Returns:** 

## `public boolean renameGroup(String from, String to, String graph)`

Rename a group.

 * **Parameters:**
   * `from` — the old name
   * `to` — the new name
   * `graph` — the graph where the group is
 * **Returns:** 

## `public boolean changeGroup(String name, JSONObject metadata, String graph)`

Change the metadata of a group.

 * **Parameters:**
   * `name` — name of the group
   * `metadata` — new metadata of the group
   * `graph` — the graph where the group is
 * **Returns:** 

## `public ArrayList<Node> findFirstNodesOfFlow (ArrayList<Node> nodes)`

Determine which nodes are the first one on the flow and give the list of them.

 * **Parameters:** `nodes` — The nodes composing the flow
 * **Returns:** The list of first nodes to execute in order to run the flow.

## `public String getComponentsLibrary()`

The components library is the library that contains all the components the user will be able to use in order to build his flow.

 * **Returns:** The name of the library

## `public String getId()`

The unique id of the Flow

 * **Returns:** The unique id of the Flow as String

## `public JSONObject getFlowObject ()`

 * **Returns:** the flow as JSONObject

## `public Edge getEdge (JSONObject src, JSONObject tgt, String graph)`

Get an edge from its source and target nodes

 * **Parameters:**
   * `src` — the source node of the edge. Src format is : {node: string(id), port: string}
   * `tgt` — the target node of the edge. Tgt format is : {node: string(id), port: string}
   * `graph` — the graph where the edge is supposed to be
 * **Returns:** the Edge if found, null if not

## `public Edge getEdge (String id)`

Get an edge from its id

 * **Parameters:** `id` — the id of the edge
 * **Returns:** the Edge if found, null if not

## `public ArrayList<Node> getNodes()`

 * **Returns:** the list of nodes

## `public ArrayList<Node> getNodes (Group g)`

Get the list of nodes of a given group.

 * **Parameters:** `g` — the group
 * **Returns:** the list of nodes that are in the given group

## `public Node getNode (String id, String graph)`

Get a node

 * **Parameters:**
   * `id` — the id of the node
   * `graph` — the graph where the node is
 * **Returns:** the Node if found, null if not

## `public Group getGroup (String name, String graph)`

Get a group

 * **Parameters:**
   * `name` — the name of the group
   * `graph` — the graph where the Group is
 * **Returns:** the Group if found, null if not

## `public Object getGraph (String graph)`

Get a graph

 * **Parameters:** `graph` — the id of the graph
 * **Returns:** the graph as an object that can be Flow or Group

## `public void setDescription(String description)`

Set the description of the flow/project.

 * **Parameters:** `description` — the new description

## `public Status getStatus()`

Get the status of the Flow

 * **Returns:** the Status instance

## `private boolean nodeExist (String id)`

Test whether a node exists or not

 * **Parameters:** `id` — the id of the node
 * **Returns:** True if the node exists

## `private boolean edgeExist (JSONObject src, JSONObject tgt)`

Test whether an edge exists or not

 * **Parameters:**
   * `src` — the source node of the edge. Src format is : {node: string(id), port: string}
   * `tgt` — the target node of the edge. Tgt format is : {node: string(id), port: string}
 * **Returns:** True if exists

## `private boolean edgeExist(String id)`

Test whether an edge exists or not

 * **Parameters:** `id` — the id of the edge
 * **Returns:** True if exists

## `private boolean graphExist (String id)`

Test whether a graph exists or not

 * **Parameters:** `id` — the id of the graph
 * **Returns:** True if exists

## `private boolean groupExist (String name)`

Test whether a group exists or not

 * **Parameters:** `name` — the name of the group
 * **Returns:** True if exists

## `private int indexOfEdge (JSONObject src, JSONObject tgt)`

Get the index of an edge in the edges array

 * **Parameters:**
   * `src` — the source node of the edge. Src format is : {node: string(id), port: string}
   * `tgt` — the target node of the edge. Tgt format is : {node: string(id), port: string}
 * **Returns:** the index of the edge, -1 if not in edges

## `private int indexOfEdge (String id)`

Get the index of an edge in the edges array

 * **Parameters:** `id` — the id of the edge
 * **Returns:** the index of the edge, -1 if not in edges

## `private int indexOfNode (String id)`

Get the index of a node in the nodes array

 * **Parameters:** `id` — the id of the node
 * **Returns:** the index of the node, -1 if not in nodes

## `private int indexOfGroup (String name)`

Get the index of a group in the groups array

 * **Parameters:** `name` — the name of the group
 * **Returns:** the index of the group, -1 if not in groups
