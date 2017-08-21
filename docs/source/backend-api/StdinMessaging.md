---
layout: default
id: StdinMessaging
title: Backend API
parent: backend-api
---
# Documentation

## `public class StdinMessaging`

http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel

Created by antoine on 10/05/2017.

## `public void handleMessage (String type, JupyterMessage message)`

Handle the given message, coming from the Stdin Channel

 * **Parameters:**
   * `type` — {String} the type of the message
   * `message` — {JupyterMessage} the message itself

## `private void handleInputRequestMessage (JupyterMessage message)`

Handle an input_request message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel

Our implementation behavior: depends on the status of the message. 1. Prompts all the UI for the answer 2. Retrieve the answer and build the message 3. Respond to the kernel with a input_reply message

 * **Parameters:** `message` — {JupyterMessage} the received message
