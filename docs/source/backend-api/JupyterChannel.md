---
layout: default
id: JupyterChannel
title: Backend API
parent: backend-api
---
# Documentation

## `public abstract class JupyterChannel implements Runnable`

A Kernel uses 5 channels to communicates on both side (client/server). All this is explained here : http://jupyter-client.readthedocs.io/en/latest/messaging.html#

The Jupyter channel is the abstract class that implements the common behavior of all the Jupyter Channels of this program.

When needed, the child classes (Heartbeat, IOPub, Shell, Control, Stdin) override the necessary methods and attributes.

Created by antoine on 28/04/17.

## `public void run()`

Run method from Runnable interface

## `public void start()`

Start the thread that makes the channel working

## `public void stop() throws InterruptedException`

Interrupt the channel by interrupting the thread

## `public void doLog (boolean log)`

If true, the channel will log every message it receives. Otherwise, doesn't log anything.

 * **Parameters:** `log` — : boolean

## `public boolean isRunning()`

Is the Channel running ?

 * **Returns:** {boolean} true if still alive, false otherwise

## `public void setIdentity(String identity)`

Set the identity of the ZMQ socket. The identity is sent as a message preceding the other messages sent with socket.sendMore and socket.send

 * **Parameters:** `identity` — : a String representing the chosen identity

## `public void doStoreHistory (boolean b)`

Set the behavior of the channel about storing messages history

 * **Parameters:** `b` — : true to store, false not to

## `private void logMessage (ArrayList<String> incomingMessage)`

 * **Parameters:** `incomingMessage` — : complete Jupyter message. Look at the Jupyter doc to know more about it

## `private void handleMessage(ArrayList<String> incomingMessage)`

 * **Parameters:** `incomingMessage` — : the received message, look at Jupyter doc to know its format

## `private boolean isUuid (String message)`

Verify whether the given message is a correct UUID (Universally Unique IDentifier)

 * **Parameters:** `message` — {String} the message to test
 * **Returns:** {boolean} True if the message is a UUID, false otherwise.

## `protected abstract void initializeThread()`

Define a initializeThread method that should be overridden by the child classes.

This method is called when the Thread start in order to initialize what needs to be initialized.
