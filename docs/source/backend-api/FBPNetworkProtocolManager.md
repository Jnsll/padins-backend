---
layout: default
id: FBPNetworkProtocolManager
title: Backend API
parent: backend-api
---
# Documentation

## `@SuppressWarnings("unchecked") public class FBPNetworkProtocolManager`

The FBPNetworkProtocolManager handle the FBP Network Protocol's compliant messages received on the socket endpoint.

Its main role is to read the protocol field of the message and redirect the message to the proper handler, and to send the already formatted messages to the clients.

It can also send a few custom messages (startnode and finishnode), handles the traceback received from the kernel and send a changenode message.

Created by antoine on 26/05/2017.

## `public void setSocket (ServerSocket socket)`

Set the attached socket. The socket will be used to send messages to the right client.

 * **Parameters:** `socket` — {ServerSocket} the connected client's socket instance

## `String getComponentsLibrary()`

Returns the name of the component library used in the workspace.

 * **Returns:** {String} the name of the component library

## `synchronized public void send (FBPMessage msg)`

Send the given message to the connected client.

 * **Parameters:** `msg` — {FBPMessage} the message to send

## `synchronized private void sendMsgToSocket (FBPMessage msg, ServerSocket socket)`

Send the given message through the given socket.

 * **Parameters:**
   * `msg` — {FBPMessage} the message to send
   * `socket` — {ServerSocket} the destination socket

## `synchronized void sendToAll (FBPMessage msg)`

Send the given message to all the clients connected on the workspace.

 * **Parameters:** `msg` — {FBPMessage} the message to send

## `synchronized public void sendError(String protocol, String error)`

Send an error message to the client.

 * **Parameters:**
   * `protocol` — {String} the protocol on which the error has been thrown.
   * `error` — {String} the error message.

## `synchronized public void sendErrorToAll(String protocol, String error)`

Send an error message to all the clients connected on the workspace.

 * **Parameters:**
   * `protocol` — {String} the protocol on which the error has been thrown.
   * `error` — {String} the error message.

## `public void sendUpdateNodeMessage (Node node)`

Send a changenode message to all clients in order to tell the UIs to update the node.

 * **Parameters:** `node` — {Node} the updated node

## `public void handleTracebackFromKernel (JSONArray traceback, Kernel k)`

Handle the traceback coming from the kernel, redirecting it to the UIs.

 * **Parameters:**
   * `traceback` — {JSONArray} the traceback, as an array on text lines.
   * `k` — {Kernel} the kernel that provide the traceback

## `private FBPMessage createErrorMessage (String protocol, String error)`

Create an FBPNP compliant error message from the given protocol and error message.

 * **Parameters:**
   * `protocol` — {String} the protocol on which the error has been thrown.
   * `error` — {String} the error message.
 * **Returns:** {FBPMessage} the FBPNP compliant error message.

## `public void sendStartNode (String id)`

Send a startnode message that say that the node with the given id has just started being executed.

 * **Parameters:** `id` — {String} the uuid of the node

## `public void sendFinishNode (String id)`

Send a stopnode message that say that the node with the given id has just stopped being executed.

 * **Parameters:** `id` — {String} the uuid of the node
