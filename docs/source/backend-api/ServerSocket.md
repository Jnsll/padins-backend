---
layout: default
id: ServerSocket
title: Backend API
parent: backend-api
---
# Documentation

## `@WebSocket public class ServerSocket`

Several clients can be connected to the same workspace. Each client communicate through a single instance of a websocket (this class).

This class implements the behavior of the websocket that will be instantiated each time a new client connects, and used each time a client communicates.

Created by antoine on 26/05/2017.

## `synchronized public boolean send (String msg)`

Send the given message over the socket.

 * **Parameters:** `msg` — {String} the message to send.
 * **Returns:** {boolean} sending confirmation.

## `private void storeSocketOnMessageHandlers ()`

Pass its instance to the objects that will need it in order to send messages to the connected client.
