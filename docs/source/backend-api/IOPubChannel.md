---
layout: default
id: IOPubChannel
title: Backend API
parent: backend-api
---
# Documentation

## `public class IOPubChannel extends JupyterChannel`

IOPub: this socket is the ‘broadcast channel’ where the kernel publishes all side effects (stdout, stderr, etc.) as well as the requests coming from any client over the shell socket and its own requests on the stdin socket. There are a number of actions in Python which generate side effects: print() writes to sys.stdout, errors generate tracebacks, etc. Additionally, in a multi-client scenario, we want all frontends to be able to know what each other has sent to the kernel (this can be useful in collaborative scenarios, for example). This socket allows both side effects and the information about communications taking place with one client over the shell channel to be made available to all clients in a uniform manner.

Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html IOPub channel documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-iopub-pub-sub-channel

EXTENDS JupyterChannel : the abstract class implementing the default behavior of a Jupyter channel.

Created by antoine on 03/05/17.
