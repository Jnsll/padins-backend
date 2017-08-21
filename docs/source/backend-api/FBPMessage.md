---
layout: default
id: FBPMessage
title: Backend API
parent: backend-api
---
# Documentation

## `@SuppressWarnings("unchecked") public class FBPMessage`

A very simple class to create and handle FBP Network Protocol compliant messages. The structure of a message is described here : https://flowbased.github.io/fbp-protocol/#message-structure

Created by antoine on 26/05/2017.

## `public String getProtocol ()`

Returns the protocol field of the message as a String. The protocols are : runtime - graph - component - network - trace

 * **Returns:** {String} the protocol field of the message.

## `public String getCommand ()`

Returns the command field of the message. The command is the name of the action to do. For example removeedge

 * **Returns:** {String} the command field of the message

## `public JSONObject getPayload()`

Returns the payload of the message, as a JSONObject. The payload contains all the interesting information to handle the message. It must be compliant with the documentation. Each command has a different payload.

 * **Returns:** {JSONObject} the payload of the message

## `public void setProtocol(String protocol)`

Set the protocol field of the message.

 * **Parameters:** `protocol` — {String} the new protocol. Must be : runtime - graph - component - network - trace

## `public void setCommand (String command)`

Set the command field of the message.

 * **Parameters:** `command` — {String} the new command

## `public void setPayload (String payload)`

Set the payload of the message, from a String.

 * **Parameters:** `payload` — {String} the new payload, as a serialized JSON object

## `public String toJSONString ()`

Serialize the message to a JSON compliant String. Commonly used to send the message through a socket.

 * **Returns:** {String} the serialized message
