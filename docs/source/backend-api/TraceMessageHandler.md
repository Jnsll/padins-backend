---
layout: default
id: TraceMessageHandler
title: Backend API
parent: backend-api
---
# Documentation

## `public class TraceMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler`

Class managing the Trace Message for the Flow-Based Programming Network Protocol To know more about this protocol, take a look at the doc on J.Paul Morisson's website : https://flowbased.github.io/fbp-protocol/#sub-protocols

Created by antoine on 26/05/2017.

## `public void handleMessage (FBPMessage message)`

Handle a message. It call the corresponding method for each supported type of message.

 * **Parameters:** `message` — : the message to handle
