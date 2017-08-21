---
layout: default
id: Workspace
title: Backend API
parent: backend-api
---
# Documentation

## `public class Workspace`

A workspace corresponds to one project, so one flow. It can have several kernels and connected users. One flow is represented by one JSON, containing all the vue structure.

Created by antoine on 25/05/2017.

## `public void newClientConnection (ServerSocket client)`

 * **Parameters:** `client` — : the socket of the client

## `public void clientDeconnection (ServerSocket client)`

 * **Parameters:** `client` — : the disconnected client

## `public void startNewKernel (String nodeId)`

Should be used each time a new Simulation or Processing block is created

 * **Returns:** : the uuid of the kernel

## `public void stopKernel (String nodeId)`

Commonly used when a node is removed

 * **Parameters:** `nodeId` — : the nodeId linked to the kernel

## `public boolean stopKernels () throws InterruptedException`

Stop all the kernels. Use only when all users have stopped the connexion or when you stop the server.

## `public void startGraph (String graph) throws NotExistingGraphException`

Start the execution of a given graph.

 * **Parameters:** `graph` — : the id of the graph. Can be the full flow or a group.
 * **Exceptions:** `NotExistingGraphException` — 

## `public void stopGraph (String graph)`

Stop a running graph

 * **Parameters:** `graph` — : the id of the graph. Can be the full flow or a group.

## `public boolean graphRunning (String graph)`

Is a graph running ?

 * **Parameters:** `graph` — : the id of the graph. Can be the full flow or a group.
 * **Returns:** true if the execution of the graph is running.

## `public void executeNode (Node node)`

Launch the execution of a given node.

 * **Parameters:** `node` — : the node to execute.

## `public void errorExecutingNode (String nodeId)`

Broadcast the information that the given node throws an error while executing in the kernel.

 * **Parameters:** `nodeId` — the id of the node.

## `public void stopNode (Node node)`

Stop the execution of a given node.

 * **Parameters:** `node` — : the node to stop.

## `public boolean isNodeRunning (String nodeId)`

Is a given node running ?

 * **Parameters:** `nodeId` — : the id of the node
 * **Returns:** True if the node is running

## `public void errorFromKernel (String error)`

Handle an error returned by the Jupyter kernel. It sends an error message to the connected UIs.

 * **Parameters:** `error` — : the error sent by the kernel.

## `public void save ()`

Save the workspace, storing the full flow as a json file in the workspace folder on the HD. Each workspace has its own folder on the HD.

## `public void sendUpdateNodeMessage (Node node)`

Send a message to the UI containing the modifications done on the given node.

 * **Parameters:** `node` — : the node for which you want to send the information to the UIs.

## `public String getName ()`

Get the name of the workspace. The name is defined by the end-user.

 * **Returns:** the name of the workspace.

## `public void setName(String name)`

Change the name of the workspace to the given value.

 * **Parameters:** `name` — the new name of the workspace

## `public ArrayList<ServerSocket> getConnectedClients()`

Get the list of the connected clients.

 * **Returns:** the list of connected clients as an array of ServerSocket.

## `public String getLibrary()`

Get the library of components used in this workspace.

 * **Returns:** the name of the library.

## `public String getUuid()`

Get the unique id of the workspace.

 * **Returns:** the uuid.

## `public Flow getFlow()`

Get the flow designed by the user. The flow is the bunch of components (nodes), linked with edges that described the process the user wants to study/simulate.

 * **Returns:** the Flow instance

## `public Kernel getKernel (String nodeId)`

Get the Kernel (so the Docker container) that corresponds and execute the given node.

 * **Parameters:** `nodeId` — {String} the id of the node you want to get its kernel
 * **Returns:** {Kernel} the kernel linked to the given node.

## `public Path getPathToWorkspaceFolder()`

Get the absolute path to the workspace folder on the machine.

 * **Returns:** the path of the workspace

## `public String getNodeIdForKernel (Kernel k)`

Get the node associated to a kernel

 * **Parameters:** `k` — the kernel
 * **Returns:** the id of the node

## `private JSONObject importFlowJSON (Path pathToFolder)`

Import a flow as a JSONObject from the given folder. The flow file must be named flow.json

 * **Parameters:** `pathToFolder` — the path to the folder containing the flow.
 * **Returns:** a JSONObject containing the flow.

## `private boolean createFolder (Path path)`

Create the folder of the workspace.

 * **Parameters:** `path` — the path to folder in which you want to create the folder.
 * **Returns:** True if the folder exists after this method run.

## `public class NotExistingGraphException extends Exception`

Exception indicating that the graph to run doesn't exist.
