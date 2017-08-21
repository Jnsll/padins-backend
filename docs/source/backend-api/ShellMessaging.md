---
layout: default
id: ShellMessaging
title: Backend API
parent: backend-api
---
# Documentation

## `public class ShellMessaging`

http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-shell-router-dealer-channel

The list of messages is : execute_request inspect_request complete_request history_request is_complete_request connect_request comm_info_request kernel_info_request shutdown_request

Created by antoine on 10/05/2017.

## `public void handleMessage (String type, JupyterMessage message)`

Handle the given message, coming from the Shell or Control Channel

 * **Parameters:**
   * `type` — {String} the type of the message
   * `message` — {JupyterMessage} the message itself

## `public String sendExecuteRequestMessage (String code)`

Implementation of execute_request message according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#execute

 * **Parameters:** `code` — : python code to execute
 * **Returns:** : the message sent through the channel

## `public String sendIntrospectionRequestMessage (String code, int cursorPos)`

Implementation of introspection inspect_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#introspection

 * **Returns:** : the message sent through the channel

## `public String sendCompletionRequestMessage (String code, int cursorPos)`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#completion

 * **Returns:** : the message sent through the channel

## `public String sendHistoryRequestMessage (int nbOfCells)`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#history

 * **Returns:** : the message sent through the channel

## `public String sendCodeCompletenessRequestMessage (String code)`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-completeness

 * **Returns:** : the message sent through the channel

## `public String sendConnectRequestMessage ()`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#connect

 * **Returns:** : the message sent through the channel

## `public String sendCommInfoRequestMessage ()`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#comm-info

 * **Returns:** : the message sent through the channel

## `public String sendKernelInfoRequestMessage ()`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info

 * **Returns:** : the message sent through the channel

## `public String sendKernelShutdownRequestMessage (boolean restart)`

Implementation of introspection complete_request according to documentation http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-shutdown

 * **Returns:** : the message sent through the channel

## `private void handleExecuteReplyMessage (JupyterMessage message)`

Handle an excute_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#execution-results

Our implementation behavior: depends on the status of the message. - OK : do nothing - ERROR : Broadcast the error to the UIs and log an error - ABORT : Broadcast the abort to the UIs and log an error

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleIntrospectionReplyMessage (JupyterMessage message)`

Handle an introspection_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#introspection

Our implementation behavior: depends on the status of the message. - OK : TODO send result to UIs - ERROR : Broadcast an error to the UIs

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleCompletionReplyMessage (JupyterMessage message)`

Handle an completion_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#completion

Our implementation behavior: depends on the status of the message. - OK : TODO send info to corresponding UI, probably sending UI username via metadata and retrieving it here - ERROR : Broadcast an error to the UIs

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleHistoryReplyMessage (JupyterMessage message)`

Handle an history_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#history

Our implementation behavior: depends on the status of the message. TODO

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleCodeCompletenessReplyMessage (JupyterMessage message)`

Handle a is_complete_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-completeness

Our implementation behavior: depends on the status of the message. TODO

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleConnectionReplyMessage (JupyterMessage message)`

Handle a connect_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#connect

Our implementation behavior: depends on the status of the message. TODO

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleCommInfoReplyMessage (JupyterMessage message)`

Handle a comm_info_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#comm-info

Our implementation behavior: depends on the status of the message. TODO

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleKernelInfoReplyMessage (JupyterMessage message)`

Handle a kernel_info_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info

Our implementation behavior: depends on the status of the message. TODO

 * **Parameters:** `message` — {JupyterMessage} the received message

## `private void handleShutdownReplyMessage (JupyterMessage message)`

Handle a shutdown_reply message, according to this doc : http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-shutdown

Our implementation behavior: depends on the status of the message. TODO

 * **Parameters:** `message` — {JupyterMessage} the received message
