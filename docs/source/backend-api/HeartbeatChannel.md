---
layout: default
id: HeartbeatChannel
title: Backend API
parent: backend-api
---
# Documentation

## `public class HeartbeatChannel extends JupyterChannel`

The Heartbeat channel is one of the five channel used to communicate with a Jupyter Kernel.

A Heartbeat is very common in Socket communication. It sends a short message, every second, for instance "ping", to the connected server and wait for its answer.

We use it in order to know if we are still connected to the server.

TODO : add disconnection monitoring.

Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html Heartbeat documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#heartbeat-for-kernels

EXTENDS JupyterChannel : the abstract class implementing the default behavior of a Jupyter channel.

Created by antoine on 03/05/17.

## `@Override public void run()`

Run methods from Runnable interface
