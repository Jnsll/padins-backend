---
layout: default
id: Edge
title: Backend API
parent: backend-api
---
# Documentation

## `public class Edge`

Visually, an edge is a link between two nodes. An edge is a part of the flow. The notion is described in the Flow-based programming paradigm.

Created by antoine on 29/05/17.

## `public String getId ()`

Give the unique id of the edge

 * **Returns:** its uuid

## `public JSONObject getSrc ()`

Give the source element of the edge.

As described in the FBP Network Protocol, the source element is composed of : - The node id - The port of the node the edge is connected to

 * **Returns:** the src el as JSONObject

## `public void setSrc (JSONObject src)`

Set the source element of the edge.

As described in the FBP Network Protocol, the source element is composed of : - The node id - The port of the node the edge is connected to

 * **Parameters:** `src` — the FBP compliant src element

## `public void setTgt (JSONObject tgt)`

Set the target element of the edge.

As described in the FBP Network Protocol, the target element is composed of : - The node id - The port of the node the edge is connected to

 * **Parameters:** `tgt` — the FBP compliant tgt element

## `public JSONObject getMetadata ()`

Give the metadata of the edhe

 * **Returns:** the metadata as a JSONObject

## `public void setMetadata(JSONObject metadata)`

Replace the metadata of the edge with the given one

 * **Parameters:** `metadata` — the new metadata

## `public String getGraph ()`

Give the graph id on which the edge is.

 * **Returns:** the unique id of the graph.

## `public JSONObject getJson()`

Give the JSONObject representing a node. The JSON has the following structure : { 'id': string, 'src': object, 'tgt': object, 'metadata': object, 'graph': string }

 * **Returns:** a JSONObject containing the edge's data

## `private void build ()`

Build the JSONObject of the edge.

The JSON has the following structure : { 'id': string, 'src': object, 'tgt': object, 'metadata': object, 'graph': string }
