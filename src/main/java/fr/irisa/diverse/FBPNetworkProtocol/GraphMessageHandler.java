package fr.irisa.diverse.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Workspace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import fr.irisa.diverse.Flow.*;

/**
 * Created by antoine on 26/05/2017.
 */
public class GraphMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler  {

    // Attributes
    private Workspace owningWorkspace;
    private Flow flow;

    // Constructor
    public GraphMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
        this.flow = owningManager.owningWorkspace.getFlow();
        this.owningWorkspace = manager.owningWorkspace;
        this.PROTOCOL = "graph";
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "clear" :
                clear();
                break;
            case "addnode" :
                addnode(message.getPayloadAsJSON());
                break;
            case "removenode" :
                removenode(message.getPayloadAsJSON());
                break;
            case "renamenode" :
                renamenode(message.getPayloadAsJSON());
                break;
            case "changenode" :
                changenode(message.getPayloadAsJSON());
                break;
            case "addedge" :
                addedge(message.getPayloadAsJSON());
                break;
            case "removeedge" :
                removeedge(message.getPayloadAsJSON());
                break;
            case "changeedge" :
                changeedge(message.getPayloadAsJSON());
                break;
            case "addinitial" :
                addinitial(message.getPayloadAsJSON());
                break;
            case "removeinitial" :
                removeinitial(message.getPayloadAsJSON());
                break;
            case "addinport" :
                addinport(message.getPayloadAsJSON());
                break;
            case "removeinport" :
                removeinport(message.getPayloadAsJSON());
                break;
            case "renameinport" :
                renameinport(message.getPayloadAsJSON());
                break;
            case "addoutport" :
                addoutport(message.getPayloadAsJSON());
                break;
            case "removeoutport" :
                removeoutport(message.getPayloadAsJSON());
                break;
            case "renameoutport" :
                renameoutport(message.getPayloadAsJSON());
                break;
            case "addgroup" :
                addgroup(message.getPayloadAsJSON());
                break;
            case "removegroup" :
                removegroup(message.getPayloadAsJSON());
                break;
            case "renamegroup" :
                renamegroup(message.getPayloadAsJSON());
                break;
            case "changegroup" :
                changegroup(message.getPayloadAsJSON());
                break;
            default :
                owningManager.sendError(PROTOCOL, "Error with message : " + message.toJSONString());
                break;
        }
    }

    /* =================================================================================================================
                                   PRIVATE METHODS TO HANDLE RECEIVED MESSAGES
       ===============================================================================================================*/

    private void clear () {
        System.out.println("You are trying to empty the graph. It is too dangerous to be implemented");
    }

    private void addnode (JSONObject payload) {
        // Retrieve needed data for addNode() method
        String id = (String) payload.get("id");
        String component = (String) payload.get("component");
        JSONObject metadata = new JSONObject();
        if (!(payload.get("metadata") instanceof String)) metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        // Add the node into the flow and if it succeed send a message back to the connected clients
        // & start a kernel if the node is a Processing or Simulation
        if(flow.addNode(id, component, metadata, graph)) {
            // Start a kernel if needed
            if (component.equals("Processing") || component.equals("Simulation")) {
                String kernelId = owningWorkspace.startNewKernel(id);
            }
            // Answer
            sendAddNodeMessage(id, graph);
            sendAddInportAndOutportForNode(flow.getNode(id, graph), graph);

        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to create node because graph " + graph + " doesn't exist");
        }
    }

    private void removenode (JSONObject payload) {
        // Retrieve needed data for removeNode() method
        String id = (String) payload.get("id");
        String graph = (String) payload.get("graph");

        // If the node is a Processing or Simulation node, stop the associated Jupyter kernel
        Node n = flow.getNode(id, graph);
        if(n.getComponent().equals("Processing") || n.getComponent().equals("Simulation")) owningWorkspace.stopKernel(id);

        // Remove the node from the flow (data structure)
        if (flow.removeNode(id, graph)) {
            // Answer
            sendRemoveNodeMessage(id, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to remove node because graph " + graph + " doesn't exist");
        }
    }

    private void renamenode (JSONObject payload) {
        // Retrieve needed data for renameNode() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        if (flow.renameNode(from, to, graph)) {
            // Answer
            sendRenameNodeMessage(from, to, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to rename node " + from);
        }

    }

    private void changenode (JSONObject payload) {
        // Retrieve needed data for changeNode() method
        String id = (String) payload.get("id");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.changeNode(id, metadata, graph)) {
            // Answer
            sendChangeNodeMessage(id, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to change node " + id);
        }
    }

    private void addedge (JSONObject payload) {
        // Retrieve needed data for addEdge() method
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.addEdge(src, tgt, metadata, graph)) {
            // Answer
            sendAddEdgeMessage(src, tgt, graph);
        } else {
            String srcNodeId = (String) src.get("node");
            String tgtNodeId = (String) src.get("node");
            System.err.println("[ERROR] Cannot create graph for src : " + srcNodeId + ", target : " + tgtNodeId + ", graph : " + graph + " because one of them doesn't exist");
        }

    }

    private void removeedge (JSONObject payload) {
        // Retrieve needed data for removeEdge() method
        String graph = (String) payload.get("graph");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");

        if (flow.removeEdge(graph, src, tgt)) {
            // Answer
            sendRemoveEdgeMessage(graph, src, tgt);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to remove edge. Maybe the graph doesn't exist or the edge has already been removed.");
        }

    }

    private void changeedge (JSONObject payload) {
        // Retrieve needed data for changeEdge() method
        String graph = (String) payload.get("graph");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");

        if (flow.changeEdge(graph, metadata, src, tgt)) {
            // Answer
            sendChangeEdgeMessage(graph, src, tgt);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to change request edge");
        }

    }

    private void addinitial (JSONObject payload) {
        // Retrieve needed data for addInitial() method
        String graph = (String) payload.get("graph");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");

        flow.addInitial(graph, metadata, src, tgt);

        // answer later

    }

    private void removeinitial (JSONObject payload) {
        // Retrieve needed data for removeInitial() method
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");
        String graph = (String) payload.get("graph");

        flow.removeInitial(graph, src, tgt);

        // answer later
    }

    private void addinport (JSONObject payload) {
        // Retrieve needed data for addInport() method
        String name = (String) payload.get("public");
        String node = (String) payload.get("node");
        String port = (String) payload.get("port");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        flow.addInport(name, node, port, metadata, graph);

        // answer later
    }

    private void removeinport (JSONObject payload) {
        // Retrieve needed data for removeInport() method
        String name = (String) payload.get("public");
        String graph = (String) payload.get("graph");

        flow.removeInport(name, graph);

        // answer later
    }

    private void renameinport (JSONObject payload) {
        // Retrieve needed data for renameInport() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        flow.renameInport(from, to, graph);

        // answer later
    }

    private void addoutport (JSONObject payload) {
        // Retrieve needed data for addOutport() method
        String name = (String) payload.get("public");
        String node = (String) payload.get("node");
        String port = (String) payload.get("port");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        flow.addOutport(name, node, port, metadata, graph);

        // answer later

    }

    private void removeoutport (JSONObject payload) {
        // Retrieve needed data for removeOutport() method
        String name = (String) payload.get("public");
        String graph = (String) payload.get("graph");

        flow.removeOutport(name, graph);

        // answer later
    }

    private void renameoutport (JSONObject payload) {
        // Retrieve needed data for renameOutport() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        flow.renameOutport(from, to, graph);

        // answer later
    }

    private void addgroup (JSONObject payload) {
        // Retrieve needed data for addGroup() method
        String name = (String) payload.get("name");
        JSONArray nodes = (JSONArray) payload.get("nodes");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.addGroup(name, nodes, metadata, graph)) {
            // Answer
            sendAddGroupMessage(name, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to add group to graph " + graph + " because it doesn't exist");
        }
    }

    private void removegroup (JSONObject payload) {
        // Retrieve needed data for removeGroup() method
        String name = (String) payload.get("name");
        String graph = (String) payload.get("graph");

        if (flow.removeGroup(name, graph)) {
            // Answer
            sendRemoveGroupMessage(name, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to remove group " + name + " because it doesn't exist or graph " + graph + " doesn't exist");
        }
    }

    private void renamegroup (JSONObject payload) {
        // Retrieve needed data for renameGroup() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        if (flow.renameGroup(from, to, graph)) {
            // Answer
            sendRenameGroupMessage(from, to, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to rename group " + from);
        }
    }

    private void changegroup (JSONObject payload) {
        // Retrieve needed data for changeGroup() method
        String name = (String) payload.get("name");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.changeGroup(name, metadata, graph)) {
            // Answer
            sendChangeGroupMessage(name, graph);
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to change group " + name + "'s metadata");
        }
    }

    /* =================================================================================================================
                                            PRIVATE METHODS TO SEND MESSAGES
       ===============================================================================================================*/

    private void sendClearMessage () {
        System.out.println("You are trying to empty the graph. It is too dangerous to be implemented");
    }

    private void sendAddNodeMessage (String id, String graph) {
        // Retrieve newly create node
        Node node = flow.getNode(id, graph);

        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", node.getId());
        payload.put("component", node.getComponent());
        payload.put("metadata", node.getMetadata());
        payload.put("graph", graph);

        // Send it
        sendMessageToAll("addnode", payload);
    }

    private void sendRemoveNodeMessage (String id, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", id);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("removenode", payload);
    }

    private void sendRenameNodeMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("renamenode", payload);
    }

    private void sendChangeNodeMessage (String id, String graph) {
        Node node = flow.getNode(id, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", node.getId());
        payload.put("metadata", node.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("changenode", payload);
    }

    private void sendAddEdgeMessage (JSONObject src, JSONObject tgt, String graph) {
        Edge edge = flow.getEdge(src, tgt, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("src", edge.getSrc());
        payload.put("tgt", edge.getTgt());
        payload.put("metadata", edge.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("addedge", payload);
    }

    private void sendRemoveEdgeMessage (String graph, JSONObject src, JSONObject tgt) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("graph", graph);
        payload.put("src", src);
        payload.put("tgt", tgt);

        // Send the message
        sendMessageToAll("removeedge", payload);
    }

    private void sendChangeEdgeMessage (String graph, JSONObject src, JSONObject tgt) {
        Edge edge = flow.getEdge(src, tgt, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("src", edge.getSrc());
        payload.put("tgt", edge.getTgt());
        payload.put("metadata", edge.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("changeedge", payload);
    }

    private void sendAddInitialMessage (JSONObject msg) {
        // Not used for now
    }

    private void sendRemoveInitialMessage (JSONObject msg) {
        // Not used for now
    }

    private void sendAddInportMessage (Port port, String node, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("public", port.getName());
        payload.put("node", node);
        payload.put("port",port.getPort());
        payload.put("metadata", port.getMetadata());
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("addinport", payload);
    }

    private void sendRemoveInportMessage (String name, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("public", name);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("removeinport", payload);
    }

    private void sendRenameInportMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("renameinport", payload);
    }

    private void sendAddOutportMessage (Port port, String node, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("public", port.getName());
        payload.put("node", node);
        payload.put("port",port.getPort());
        payload.put("metadata", port.getMetadata());
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("addoutport", payload);
    }

    private void sendRemoveOutportMessage (String name, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("public", name);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("removeoutport", payload);
    }

    private void sendRenameOutportMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("renameoutport", payload);
    }

    private void sendAddGroupMessage (String name, String graph) {
        Group group = flow.getGroup(name, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("nodes", fr.irisa.diverse.Utils.JSON.jsonArrayListToString(group.getNodes()));
        payload.put("metadata", group.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("addgroup", payload);
    }

    private void sendRemoveGroupMessage (String name, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("removegroup", payload);
    }

    private void sendRenameGroupMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("renamegroup", payload);
    }

    private void sendChangeGroupMessage (String name, String graph) {
        Group group = flow.getGroup(name, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("metadata", group.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("changegroup", payload);
    }

    /* =================================================================================================================
                      PRIVATE METHODS TO SEND MESSAGES. METHODS THAT ARE SPECIFICALLY FOR THIS PROGRAM
       ===============================================================================================================*/

    private void sendAddInportAndOutportForNode (Node node, String graph) {
        Ports inports = node.getInports();
        Ports outports = node.getOutports();

        // Send one message per port in inports
        for (Object o : inports) {
            if (o instanceof Port) {
                Port port = (Port) o;
                sendAddInportMessage(port, node.getId(), graph);
            }
        }

        // Send one message per port in outports
        for (Object o : outports) {
            if (o instanceof Port) {
                Port port = (Port) o;
                sendAddOutportMessage(port, node.getId(), graph);
            }
        }
    }

}

