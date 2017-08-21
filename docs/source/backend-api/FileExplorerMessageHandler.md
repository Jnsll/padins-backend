---
layout: default
id: FileExplorerMessageHandler
title: Backend API
parent: backend-api
---
# Documentation

## `@SuppressWarnings("unchecked") public class FileExplorerMessageHandler implements MessageHandler.Whole<FBPMessage>`

The FileExplorerMessageHandler implements a messaging protocol that provides a file-explorer service. Each workspace has its own directory and cannot access other workspaces' directories.

The service provide one endpoint : - getnodes : returns the file structure of the workspace's, from its root directory, as a tree.

In order to keep consistent the format of messages exchanged between the frontend and the backend, we stuck with using FBP-formatted messages.They are implemented in fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPMessage

A file upload service is implemented as a REST full API in fr.irisa.diverse.Webserver.Servlets.UploadServlet

INTERFACES IMPLEMENTATIONS The class implements MessageHandler.Whole in order to bring formalism. It also makes it usable as the MessageHandler of a socket.

Created by antoine on 23/06/17.

## `public void setSocket (ServerSocket socket)`

Set the client's socket instance.

 * **Parameters:** `socket` — {ServerSocket} the client socket

## `private void sendNodes()`

Send a "nodes" message containing the file structure of the workspace's root directory as a tree.

## `private JSONArray rootFolderStructure ()`

Returns the root directory's file structure of a workspace as a tree. It goes through all the files and subdirectories so all files are listed in the tree.

A folder content is structure as follow : Tree : { name: String, id: String, children : Array<Tree>, isExpanded: Boolean }

 * **Returns:** {JSONArray} the file structure as a tree, respecting the format described above.

## `private JSONArray folderStructure (String path)`

Returns the file structure of the given path as a tree. It includes all its subdirectories structure.

A folder content is structure as follow : Tree : { name: String, id: String, children : Array<Tree>, isExpanded: Boolean }

 * **Parameters:** `path` — {String} absolute path to the directory
 * **Returns:** {JSONArray} the content of the directory, as a tree. Formatted as described above.
