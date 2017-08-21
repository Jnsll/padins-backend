---
layout: default
id: ComponentMessageHandler
title: Backend API
parent: backend-api
---
# Documentation

## `public class ComponentMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler`

Class managing the Component messages for the Flow-Based Programming Network Protocol To know more about this protocol, take a look at the doc on J.Paul Morisson's website : https://flowbased.github.io/fbp-protocol/#sub-protocols

Created by antoine on 26/05/2017.

## `public void handleMessage (FBPMessage message)`

Handle a new incoming message on the Component protocol. It will redirect the message to the proper method

 * **Parameters:** `message` — : the message to handle

## `private void list()`

Request the list of the currently available components. Will respond with one 'component' message per available component

## `private void getsource(FBPMessage message)`

Request for the source code of a given component. Will be responded with a `source` message.

 * **Parameters:** `message` — : received message

## `private void sendComponentMessage (Component component)`

Send a component message to the client that made a request

 * **Parameters:** `component` — : the component object to send

## `private void sendComponentReadyMessage ()`

Send a componentsready message to the client that requested a list of component. This message is used to prevent the UI that all the other components have been sent.

## `@SuppressWarnings("unchecked") private void sendSourceMessage (String library, String component)`

Send a source message as described in the doc here : https://flowbased.github.io/fbp-protocol/#component-source

 * **Parameters:**
   * `library` — : the library of components
   * `component` — : the component for which to send the source code
