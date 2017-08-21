---
layout: default
id: Root
title: Backend API
parent: backend-api
---
# Documentation

## `public class Root`

This is the Root singleton of the project that loads the workspaces on startup and is used across the project to manage the workspaces.

Created by antoine on 25/05/2017.

## `public void createWorkspace (String name)`

Create a new workspace. A workspace approximately corresponds to one project on the IDE.

 * **Parameters:** `name` — of the workspace/project
 * **Returns:** the uuid of the newly create workspace

## `public boolean deleteWorkspace (String uuid, String name)`

Delete a workspace based on its uuid. Be careful with this method ! It will remove all the files in the project, the flow, etc... and it cannot be undone

 * **Parameters:** `uuid` — : the id of the workspace to delete
 * **Returns:** True if the workspace has been deleted

## `public boolean hasWorkspace (String workspace)`

Tell whether the given workspace exist

 * **Parameters:** `workspace` — The ID of the workspace
 * **Returns:** True if the workspace exists

## `public Workspace getWorkspace (String id)`

Give the requested workspace

 * **Parameters:** `id` — The ID of the request workspace
 * **Returns:** The workspace instance

## `public Map<String, Workspace> getWorkspaces()`

 * **Returns:** the Map of Name <-> Workspace instance

## `private void importWorkspace (String uuid)`

Import a workspace with the given uuid

 * **Parameters:** `uuid` — the unique id of the workspace to import.

## `private void verifyStorageFolderExists ()`

Verify that the folder where we storage all the workspaces' data exsits. If it doesn't, we create it.

## `private void addDefaultContent ()`

Add the default content that must contain the project storage folder into it. This default content is in src/main/resources/default_storage_directory_content

## `private void loadStoredWorkspaces ()`

Load all the workspaces stored on the HD and store them in the workspaces attribute of the class.
