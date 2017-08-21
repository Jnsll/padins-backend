---
layout: default
id: WorkspacesServlet
title: Backend API
parent: backend-api
---
# Documentation

## `@SuppressWarnings("unchecked") public class WorkspacesServlet extends HttpServlet`

Provides a RESTFul service to manage the workspaces.

A workspace correspond to a project. Each workspace has one (work)flow/graph and an associated directory to let the user import all the files she needs.

Implements : GET, PUT, POST, DELETE

Created by antoine on 06/06/17.

## `private JSONArray getWorkspacesList ()`

Returns the list of existing workspaces

 * **Returns:** {ArrayList} the list of existing workspaces
