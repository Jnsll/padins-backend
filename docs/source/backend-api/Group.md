---
layout: default
id: Group
title: Backend API
parent: backend-api
---
# Documentation

## `public class Group`

This is a concept of the flow-based programming network protocol, as described here : https://flowbased.github.io/fbp-protocol/#graph-addgroup

Created by antoine on 29/05/17.

## `public String getId ()`

Get the unique id of the group

 * **Returns:** {String} the unique id of the group

## `public String getName()`

Get the name of the group. The name is user-defined.

 * **Returns:** {String} the name of the group

## `public JSONObject getMetadata()`

Get the metadata of the group

 * **Returns:** {JSONObject} an object containing all the metadata of the group

## `public String getGraph()`

Get the graph (the root flow or another group) into which this group is.

 * **Returns:** {String} the id of the graph

## `public JSONArray getNodes()`

Get the list of nodes that are in this group

 * **Returns:** {JSONArray} the array containing the ids of the nodes

## `public void setName(String name)`

Set the name of the group

 * **Parameters:** `name` — {string} the new name of the graph

## `public void setMetadata(JSONObject metadata)`

Update the metadata

 * **Parameters:** `metadata` — {JSONObject} a json containing all the metadata

## `public JSONObject getJson()`

Get the group as a JSONObject. It will contain : - id {String} - name {String} - metadata {JSONObject} - graph {String} - nodes {Array<String>}

 * **Returns:** {JSONObject} the object containing all the data of the group

## `public Status getStatus()`

Get the status of the group : running or not and a few more information. See class Status

 * **Returns:** {Status} the status of the group.

## `private void build ()`

Build the json object representing the group. The json is used to send all the information of this group to any other service.

The JSONObject contains : - id {String} - name {String} - metadata {JSONObject} - graph {String} - nodes {Array<String>}
