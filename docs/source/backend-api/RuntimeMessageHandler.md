---
layout: default
id: RuntimeMessageHandler
title: Backend API
parent: backend-api
---
# Documentation

## `@SuppressWarnings("unchecked") public class RuntimeMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler`

Class managing the Runtime Message for the Flow-Based Programming Network Protocol To know more about this protocol, take a look at the doc on J.Paul Morisson's website : https://flowbased.github.io/fbp-protocol/#sub-protocols

Created by antoine on 26/05/2017.

## `public void handleMessage (FBPMessage message)`

Handle a message. It call the corresponding method for each supported type of message.

 * **Parameters:** `message` — : the message to handle

## `private void getruntime ()`

Handle a "getruntime" message by answering with a "runtime" message.

https://flowbased.github.io/fbp-protocol/#runtime-getruntime

## `private void packet ()`

Handle a "packet" message.

https://flowbased.github.io/fbp-protocol/#runtime-packet

## `private void sendPacketMessage (String port, String event, String graph, JSONObject payloadToSend)`

Send a packet message.

https://flowbased.github.io/fbp-protocol/#runtime-packet

 * **Parameters:**
   * `port` — {String} port name for the input or output port
   * `event` — {String} packet event
   * `graph` — {String} graph the action targets
   * `payloadToSend` — {JSONObject} payload for the packet. Used only with begingroup (for group names) and data packets

## `private void sendPortsMessage ()`

Send a ports message as a response to packet or each time the available ports change.

https://flowbased.github.io/fbp-protocol/#runtime-ports

## `private void sendRuntimeMessage ()`

Send a "runtime" message.

https://flowbased.github.io/fbp-protocol/#runtime-runtime
