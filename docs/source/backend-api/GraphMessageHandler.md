---
layout: default
id: GraphMessageHandler
title: Backend API
parent: backend-api
---
# Documentation

## `@SuppressWarnings("unchecked") public class GraphMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler`

Class managing the Graph Message for the Flow-Based Programming Network Protocol To know more about this protocol, take a look at the doc on J.Paul Morisson's website : https://flowbased.github.io/fbp-protocol/#sub-protocols

Created by antoine on 26/05/2017.

## `public void handleMessage (FBPMessage message)`

Handle a message. It call the corresponding method for each supported type of message.

 * **Parameters:** `message` — : the message to handle

## `private void clear ()`

Clear the content of the graph. https://flowbased.github.io/fbp-protocol/#graph-clear

## `private void addnode (JSONObject payload)`

Handle a "addnode" message by adding a new Node object into the list of nodes in the Flow object.

https://flowbased.github.io/fbp-protocol/#graph-addnode

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void removenode (JSONObject payload)`

Handle a "removenode" message by removing the Node object with the given id, from the Flow object.

https://flowbased.github.io/fbp-protocol/#graph-removenode

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void renamenode (JSONObject payload)`

Handle a "renamenode" message by changing its id.

https://flowbased.github.io/fbp-protocol/#graph-renamenode

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void changenode (JSONObject payload)`

Handle a "changenode" message by updating the metadata field of the Node object with the given id, from the Flow object.

https://flowbased.github.io/fbp-protocol/#graph-changenode

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void addedge (JSONObject payload)`

Handle a "addedge" message by adding a new Edge object into the Flow. The edge is created from the data we retrieve in the given payload object, in accordance to the FBPNP documentation.

https://flowbased.github.io/fbp-protocol/#graph-addedge

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void removeedge (JSONObject payload)`

Handle a "removeedge" message by removing the Edge object with the given id, from the Flow object.

https://flowbased.github.io/fbp-protocol/#graph-removeedge

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void changeedge (JSONObject payload)`

Handle a "changeedge" message by updating the metadata field of the Edge object with the given id, from the Flow object.

https://flowbased.github.io/fbp-protocol/#graph-changeedge

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void addinitial (JSONObject payload)`

Handle a "addinitial" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-addinitial

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void removeinitial (JSONObject payload)`

Handle a "removeinitial" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-removeinitial

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void addinport (JSONObject payload)`

Handle a "addinport" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-addinport

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void removeinport (JSONObject payload)`

Handle a "removeinport" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-removeinport

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void renameinport (JSONObject payload)`

Handle a "renameinport" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-renameinport

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void addoutport (JSONObject payload)`

Handle a "addoutport" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-addoutport

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void removeoutport (JSONObject payload)`

Handle a "removeoutport" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-removeoutport

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void renameoutport (JSONObject payload)`

Handle a "renameoutport" message. Don't do anything for now.

https://flowbased.github.io/fbp-protocol/#graph-renameoutport

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void addgroup (JSONObject payload)`

Handle a "addgroup" message by adding a group to the graph.

https://flowbased.github.io/fbp-protocol/#graph-addgroup

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void removegroup (JSONObject payload)`

Handle a "removegroup" message by removing a group from the graph.

https://flowbased.github.io/fbp-protocol/#graph-removegroup

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void renamegroup (JSONObject payload)`

Handle a "renamegroup" message by renaming an existing group from the graph.

https://flowbased.github.io/fbp-protocol/#graph-renamegroup

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void changegroup (JSONObject payload)`

Handle a "changegroup" message by updating its metadata.

https://flowbased.github.io/fbp-protocol/#graph-changegroup

 * **Parameters:** `payload` — {JSONObject} the payload from the received message

## `private void sendClearMessage ()`

Send a "clear" message on the graph protocol

https://flowbased.github.io/fbp-protocol/#graph-clear

## `private void sendAddNodeMessage (String id, String graph)`

Send a "addnode" message for the Node with the given id in the given graph.

https://flowbased.github.io/fbp-protocol/#graph-addnode

 * **Parameters:**
   * `id` — {String} id of the new node.
   * `graph` — {String} id of the graph on which to create the node.

## `private void sendRemoveNodeMessage (String id, String graph)`

Send a "removenode" message in order to remove the node with the given id from the graph.

https://flowbased.github.io/fbp-protocol/#graph-removenode

 * **Parameters:**
   * `id` — {String} id of the node to remove.
   * `graph` — {String} id of the graph on which to remove the node.

## `private void sendRenameNodeMessage (String from, String to, String graph)`

Send a "renamenode" message in order to replace the node with the new given id.

https://flowbased.github.io/fbp-protocol/#graph-renamenode

 * **Parameters:**
   * `from` — {String} previous id
   * `to` — {String} new id
   * `graph` — {String} id of the graph the node is on

## `private void sendChangeNodeMessage (String id, String graph)`

Send a "changenode" message in order to update its metadata.

https://flowbased.github.io/fbp-protocol/#graph-changenode

 * **Parameters:**
   * `id` — {String} the id of the node
   * `graph` — {String} the graph the node is on

## `private void sendAddEdgeMessage (JSONObject src, JSONObject tgt, String graph)`

Send a "addedge" message in order to create a new edge that connects two existing nodes.

https://flowbased.github.io/fbp-protocol/#graph-addedge

 * **Parameters:**
   * `src` — {JSONObject} the node's id, port and index of the source node
   * `tgt` — {JSONObject} the node's id, port and index of the target node
   * `graph` — {String} the graph the edge will be on

## `private void sendRemoveEdgeMessage (String id, String graph, JSONObject src, JSONObject tgt)`

Send a "removeedge" message in order to remove the edge from the graph.

https://flowbased.github.io/fbp-protocol/#graph-removeedge

 * **Parameters:**
   * `id` — {String} the id of the edge
   * `graph` — {String} the id of the graph the edge is on
   * `src` — {JSONObject} the node's id, port and index of the source node
   * `tgt` — {JSONObject} the node's id, port and index of the target node

## `private void sendChangeEdgeMessage (String graph, JSONObject src, JSONObject tgt)`

Send a "changeedge" message in order to connect an edge update its metadata.

https://flowbased.github.io/fbp-protocol/#graph-changeedge

 * **Parameters:**
   * `graph` — {String} the id of the graph the edge is on
   * `src` — {JSONObject} the node's id, port and index of the source node
   * `tgt` — {JSONObject} the node's id, port and index of the target node

## `private void sendAddInitialMessage (JSONObject msg)`

Send a "addinitial" message. Behavior and interest to find ...

https://flowbased.github.io/fbp-protocol/#graph-addinitial

 * **Parameters:** `msg` — {JSONObject} the msg to send

## `private void sendRemoveInitialMessage (JSONObject msg)`

Send a "removeinitial" message. Behavior and interest to find ...

https://flowbased.github.io/fbp-protocol/#graph-removeinitial

 * **Parameters:** `msg` — {JSONObject} the msg to send

## `private void sendAddInportMessage (Port port, String node, String graph)`

Send a "addinport" message.

https://flowbased.github.io/fbp-protocol/#graph-addinport

 * **Parameters:**
   * `port` — {Port} the port to add
   * `node` — {String} the id of the node on which to add the port
   * `graph` — {String} the id of the graph the node is on

## `private void sendRemoveInportMessage (String name, String graph)`

Send a "removeinport" message.

https://flowbased.github.io/fbp-protocol/#graph-removeinport

 * **Parameters:**
   * `name` — {String} the name of the port to delete
   * `graph` — {String} the id of the graph the port is on

## `private void sendRenameInportMessage (String from, String to, String graph)`

Send a "renameinport" message.

https://flowbased.github.io/fbp-protocol/#graph-renameinport

 * **Parameters:**
   * `from` — {String} original exported port name
   * `to` — {String} new exported port name
   * `graph` — {String} graph the action targets

## `private void sendAddOutportMessage (Port port, String node, String graph)`

Send a "addoutport" message.

https://flowbased.github.io/fbp-protocol/#graph-addoutport

 * **Parameters:**
   * `port` — {Port} the new port
   * `node` — {String} the node's id
   * `graph` — {String} the id of the graph the action targets

## `private void sendRemoveOutportMessage (String name, String graph)`

Send a "removeoutport" message. Remove an exported port in the graph.

https://flowbased.github.io/fbp-protocol/#graph-removeoutport

 * **Parameters:**
   * `name` — {String} name of the exported port
   * `graph` — {String} id of the graph the action targets

## `private void sendRenameOutportMessage (String from, String to, String graph)`

Send a "renameoutport" message. Rename an exported port in the graph.

https://flowbased.github.io/fbp-protocol/#graph-renameoutport

 * **Parameters:**
   * `from` — {String} previous name
   * `to` — {String} new name
   * `graph` — {String} id of the graph the action targets

## `private void sendAddGroupMessage (String name, String graph)`

Send a "addgroup" message. Add a group to the graph

https://flowbased.github.io/fbp-protocol/#graph-addgroup

 * **Parameters:**
   * `name` — {String} name of the group
   * `graph` — {String} id of the graph the action targets

## `private void sendRemoveGroupMessage (String name, String graph)`

Send a "removegroup" message. Remove a group from the graph

https://flowbased.github.io/fbp-protocol/#graph-removegroup

 * **Parameters:**
   * `name` — {String} name of the group
   * `graph` — {String} id of the graph the action targets

## `private void sendRenameGroupMessage (String from, String to, String graph)`

Send a "removegroup" message. Rename a group in the graph

https://flowbased.github.io/fbp-protocol/#graph-renamegroup

 * **Parameters:**
   * `from` — {String} the previous name
   * `to` — {String} the new name
   * `graph` — {String} id of the graph the action targets

## `private void sendChangeGroupMessage (String name, String graph)`

Send a "changegroup" message. Change a group's metadata.

https://flowbased.github.io/fbp-protocol/#graph-changegroup

 * **Parameters:**
   * `name` — {String} name of the group
   * `graph` — {String} id of the graph the action targets

## `private void sendAddInportAndOutportForNode (Node node, String graph)`

Send addinport and addoutport message for the given node.

 * **Parameters:**
   * `node` — {Node}
   * `graph` — {String} the id of the graph the action targets
