---
layout: default
id: JupyterMessage
title: Backend API
parent: backend-api
---
# Documentation

## `class JupyterMessage`

Data structure of a Jupyter message. The documentation is available here : http://jupyter-client.readthedocs.io/en/latest/messaging.html#general-message-format

Created by antoine on 10/05/2017.

## `public JupyterMessage(Kernel kernel, String msg_type)`

Constructor with minimal number of arguments

## `public JupyterMessage(Kernel kernel, String msg_type, JSONObject parent_header, JSONObject metadata, JSONObject content)`

Complete constructor for message to send

 * **Parameters:**
   * `kernel` — : source kernel
   * `msg_type` — : the type of message to send
   * `parent_header` — :  dict
   * `metadata` — :  dict
   * `content` — : dict

## `public JupyterMessage(Kernel kernel, ArrayList<String> incomingMessage)`

 * **Parameters:** `incomingMessage` — : Array of String containing the parts of the message

## `public JSONObject getHeader ()`

Get the header of the message as a JSONObject. The header contains : String msg_id, String username, String session, String date, String msg_type, String version="5.0"

 * **Returns:** {JSONObject} the header of the message.

## `public void setParentHeader (JSONObject parent_header)`

Set the parent header of the message.

Use it when the message you create respond to another message.

The parent_header must contain : String msg_id, String username, String session, String date, String msg_type, String version="5.0"

 * **Parameters:** `parent_header` — {JSONObject} the parent header

## `public JSONObject getParentHeader ()`

Get the parent header of the message.

The parent_header must contain : String msg_id, String username, String session, String date, String msg_type, String version="5.0"

 * **Returns:** {JSONObject} the parent header

## `public void setMetadata (JSONObject metadata)`

Set the metadata part of the message. Its content is free.

 * **Parameters:** `metadata` — {JSONObject} the metadata

## `public String getHmac ()`

Get the HMAC of the message

 * **Returns:** {String} the HMAC

## `public JSONObject getMetadata ()`

Get the metadata of the message. Its content is free.

 * **Returns:** {JSONObject} the metadata

## `public String getUuid ()`

Get the universally unique identifier (UUID) of the message

 * **Returns:** {String} the UUID of the message

## `public void setContent (JSONObject content)`

Set the content part of the message. It must be a JSON. Its content depends on the type of message, according to this documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html

 * **Parameters:** `content` — {JSONObject} the content part.

## `public JSONObject getContent ()`

Get the content part of the message. It must be a JSON. Its content depends on the type of message, according to this documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html

 * **Returns:** {JSONObject} the content of the message.

## `public String[] getMessageToSend ()`

Get the message serialized in the proper format in order to send it through the channel, using the ZMQ library.

 * **Returns:** {String[]} the serialized message

## `private String generateDate ()`

Generate an ISO 8061 compliant timestamp

 * **Returns:** : String - the timestamp

## `private String generateHmac()`

Generate the Jupypter messaging protocol compliant hmac

 * **Returns:** : a hmac for the message to send

## `private void buildMessage ()`

Build the message in accordance with Jupyter messaging specification
