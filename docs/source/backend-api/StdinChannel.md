---
layout: default
id: StdinChannel
title: Backend API
parent: backend-api
---
# Documentation

## `public class StdinChannel extends JupyterChannel`

stdin: this ROUTER socket is connected to all frontends, and it allows the kernel to request input from the active frontend when raw_input() is called. The frontend that executed the code has a DEALER socket that acts as a ‘virtual keyboard’ for the kernel while this communication is happening (illustrated in the figure by the black outline around the central keyboard). In practice, frontends may display such kernel requests using a special input widget or otherwise indicating that the user is to type input for the kernel instead of normal commands in the frontend.

Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html Stdin documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel

Created by antoine on 03/05/17.

## `public void send (String[] message)`

Send a message as bytes, needed by Jupyter

 * **Parameters:** `message` — : the message to send to the shell
