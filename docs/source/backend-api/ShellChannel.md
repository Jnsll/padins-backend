---
layout: default
id: ShellChannel
title: Backend API
parent: backend-api
---
# Documentation

## `public class ShellChannel extends JupyterChannel`

This class is used to create both the shell and control channels.

Shell: this single ROUTER socket allows multiple incoming connections from frontends, and this is the socket where requests for code execution, object information, prompts, etc. are made to the kernel by any frontend. The communication on this socket is a sequence of request/reply actions from each frontend and the kernel.

Control: This channel is identical to Shell, but operates on a separate socket, to allow important messages to avoid queueing behind execution requests (e.g. shutdown or abort).

Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html Shell documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-shell-router-dealer-channel

EXTENDS JupyterChannel : the abstract class implementing the default behavior of a Jupyter channel.

Created by antoine on 03/05/17.

## `public void send (String[] message)`

Send a message as bytes, needed by Jupyter

 * **Parameters:** `message` — : the message to send to the shell

## `public void sendKernelInfoRequest()`

Send a KernelInfoRequest message.

Here are more information : http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info
