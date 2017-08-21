---
layout: default
id: IOPubMessaging
title: Backend API
parent: backend-api
---
# Documentation

## `class IOPubMessaging`

IOPub Messaging handle messages on the IOPub channel, according to the documentation of Messaging in Jupyter.

Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html IOPub channel documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-iopub-pub-sub-channel

Created by antoine on 10/05/2017.

## `public void handleMessage (String type, JupyterMessage message)`

Handle the given message, coming from the IOPub Channel

 * **Parameters:**
   * `type` — {String} the type of the message
   * `message` — {JupyterMessage} the message itself

## `private void handleStatusMessage (JupyterMessage message)`

Handle a status message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-status

Our implementation behavior: store the status in the Kernel object.

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleErrorMessage(JupyterMessage message)`

Handle a error message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#execution-errors

Our implementation behavior: retrieve the Traceback and transmit it to the connected UIs

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleExecuteResultMessage(JupyterMessage message)`

Handle an execute_result message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#id6

Our implementation behavior: store the execution count and log the result

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleCodeInputMessage(JupyterMessage message)`

Handle an execute_input message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-inputs

Our implementation behavior: do nothing

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleDisplayDataMessage(JupyterMessage message)`

Handle a display_data message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#display-data

Our implementation behavior: log the data. No need to send it to UIs because there is a much more powerful component on the frontend to display data.

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleUpdateDisplayDataMessage(JupyterMessage message)`

Handle an update_display_data message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#update-display-data

Our implementation behavior: do nothing. No need to send it to UIs because there is a much more powerful component on the frontend to display data.

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleStreamMessage(JupyterMessage message)`

Handle a stream message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#streams-stdout-stderr-etc

Our implementation behavior: - STDOUT : we use the stdout to retrieve the data to share across nodes. So, we store the lines of the stdout in a variable. Then the kernel handles it by storing the data into the corresponding node.

-STDERR : we do not handle the stderr for now. The error message sends the traceback use for debugging.

 * **Parameters:** `message` — {JupyterMessage} the received message
