---
layout: default
id: Webserver
title: Backend API
parent: backend-api
---
# Documentation

## `public class Webserver implements Runnable`

A HTTP REST webserver that serves the static content and provides endpoints to discover the available workspaces. After the client choose the workspace she wants to connect to, the communications switch to the websocket.

SINGLETON

Created by antoine on 06/06/17.

## `public static Webserver getInstance ()`

Returns the instance of the webserver.

 * **Returns:** {Webserver} the webserver singleton

## `public void stop ()`

Stop the webserver

## `public void run ()`

Configure and start the webserver
