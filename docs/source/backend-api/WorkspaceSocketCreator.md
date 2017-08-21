---
layout: default
id: WorkspaceSocketCreator
title: Backend API
parent: backend-api
---
# Documentation

## `public class WorkspaceSocketCreator implements WebSocketCreator`

Handle the request of a user trying to connect to a workspace.

The client implementation uses the subprotocol field to send the uuid of the workspace it wants to connect to.

Created by antoine on 15/06/2017.
