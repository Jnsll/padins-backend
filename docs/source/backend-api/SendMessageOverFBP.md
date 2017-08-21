---
layout: default
id: SendMessageOverFBP
title: Backend API
parent: backend-api
---
# Documentation

## `abstract class SendMessageOverFBP`

Abstract class that implements methods related to sending messages from the server to the UIs, over the Flow-Based Programming Network Protocol.

Created by antoine on 30/05/17.

## `void sendMessage(String command, JSONObject payload)`

Send a message to only one client on the UI, the same as the one that sent a request.

 * **Parameters:**
   * `command` — the type of the message to send
   * `payload` — the interesting content of the message

## `void sendMessageToAll(String command, JSONObject payload)`

Send a message to all the clients connected to the workspace/project.

 * **Parameters:**
   * `command` — the type of the message to send
   * `payload` — the interesting content of the message

## `void sendError (String message)`

Send an error message to only one client on the UI, the same as the one that sent a request.

 * **Parameters:** `message` — The text of the error to send

## `void sendErrorToAll (String message)`

Send an error message to all the clients connected to the workspace/project.

 * **Parameters:** `message` — The text of the error to send
