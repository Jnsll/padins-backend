---
layout: default
id: Manager
title: Backend API
parent: backend-api
---
# Documentation

## `public class Manager`

It is the main class to use in order to handle the messages coming from a Jupyter Kernel.

It has a main method that is handleMessage. This method takes the sourceChannel and the incoming message and redirect it to the proper handler.

Created by antoine on 16/05/2017.

## `public void handleMessage (String sourceChannel, ArrayList<String> incomingMessage)`

Handles any Jupyter Message coming from a channel.

 * **Parameters:**
   * `sourceChannel` — {String} the name of the channel the message comes from
   * `incomingMessage` — {String[]} the received message

## `public ShellMessaging sendMessageOnShell ()`

Send a message on the shell channel, using this method's returned object followed by a call to the method sending the message you want to send.

 * **Returns:** {ShellMessaging} the component that handle sending correctly formatted messages over the shell channel.

## `public StdinMessaging respondeOnStdin ()`

Respond to a prompt request on Stdin, using this method's returned object followed by a call to the method to answer on the channel.

 * **Returns:** {StdinMessaging} the component that handle sending correctly formatted messages over the stdin channel

## `public ShellMessaging sendMessageOnControl ()`

Send a message on the control channel, using this method's returned object followed by a call to the method sending the message you want to send.

 * **Returns:** {ShellMessaging} the component that handle sending correctly formatted messages over the control channel.

## `private boolean hmacIsCorrect(JupyterMessage message)`

Verify that the HMAC in the given message is correct, according to the Jupyter documentation. http://jupyter-client.readthedocs.io/en/latest/messaging.html#the-wire-protocol

 * **Parameters:** `message` — {JupyterMessage} the message to test
 * **Returns:** {boolean} True if correct, false if not

## `private void handleHeader(JSONObject header)`

Handle the header from the received message. The header contains : String msg_id, String username, String session, String date, String msg_type, String version="5.0"

 * **Parameters:** `header` — {JSONObject} the header to handle its content

## `private void handleUUID (String uuid)`

Handle the given uuid by using it as the identity of the kernel.

 * **Parameters:** `uuid` — {String} the uuid to handle

## `private void setKernelsIdentity (String kernelId)`

Set the ZMQ identity, used in messages for the kernel on this server-side. The kernel identity (from docker) is formatted as : kernel.{u-u-i-d}.{message} We retrieve the u-u-i-d and store it as our kernel's identity

 * **Parameters:** `kernelId` — : kernel's uuid retrieve from the first message coming from the jupyter kernel
