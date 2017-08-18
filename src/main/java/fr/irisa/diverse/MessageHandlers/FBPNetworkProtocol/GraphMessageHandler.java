package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Workspace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import fr.irisa.diverse.Flow.*;

/**
 * Class managing the Graph Message for the Flow-Based Programming Network Protocol
 * To know more about this protocol, take a look at the doc on J.Paul Morisson's website :
 * https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
@SuppressWarnings("unchecked")
public class GraphMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler  {

    // Attributes
    private Workspace owningWorkspace;
    private Flow flow;

    /* =================================================================================================================
                                                    CONSTRUCTOR
       ===============================================================================================================*/

    GraphMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
        this.flow = owningManager.owningWorkspace.getFlow();
        this.owningWorkspace = manager.owningWorkspace;
        this.PROTOCOL = "graph";
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    /**
     * Handle a message.
     * It call the corresponding method for each supported type of message.
     *
     * @param message : the message to handle
     */
    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "clear" :
                clear();
                break;
            case "addnode" :
                addnode(message.getPayload());
                break;
            case "removenode" :
                removenode(message.getPayload());
                break;
            case "renamenode" :
                renamenode(message.getPayload());
                break;
            case "changenode" :
                changenode(message.getPayload());
                break;
            case "addedge" :
                addedge(message.getPayload());
                break;
            case "removeedge" :
                removeedge(message.getPayload());
                break;
            case "changeedge" :
                changeedge(message.getPayload());
                break;
            case "addinitial" :
                addinitial(message.getPayload());
                break;
            case "removeinitial" :
                removeinitial(message.getPayload());
                break;
            case "addinport" :
                addinport(message.getPayload());
                break;
            case "removeinport" :
                removeinport(message.getPayload());
                break;
            case "renameinport" :
                renameinport(message.getPayload());
                break;
            case "addoutport" :
                addoutport(message.getPayload());
                break;
            case "removeoutport" :
                removeoutport(message.getPayload());
                break;
            case "renameoutport" :
                renameoutport(message.getPayload());
                break;
            case "addgroup" :
                addgroup(message.getPayload());
                break;
            case "removegroup" :
                removegroup(message.getPayload());
                break;
            case "renamegroup" :
                renamegroup(message.getPayload());
                break;
            case "changegroup" :
                changegroup(message.getPayload());
                break;
            default :
                sendError("Error with message : " + message.toJSONString());
                break;
        }
    }

    /* =================================================================================================================
                                   PRIVATE METHODS TO HANDLE RECEIVED MESSAGES
       ===============================================================================================================*/

    /**
     * Clear the content of the graph.
     * https://flowbased.github.io/fbp-protocol/#graph-clear
     */
    private void clear () {
        System.out.println("You are trying to empty the graph. It is too dangerous to be implemented");
    }

    /**
     * Handle a "addnode" message by adding a new Node object into the list of nodes in the Flow object.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addnode
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void addnode (JSONObject payload) {
        // Retrieve needed data for addNode() method
        String id = (String) payload.get("id");
        String component = (String) payload.get("component");
        JSONObject metadata = new JSONObject();
        if (!(payload.get("metadata") instanceof String)) metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        Component c = ComponentsUtils.getComponent(owningWorkspace.getLibrary(), component);
        boolean executable = false;
        if (c != null) {
            executable = c.isExecutable();
            metadata.put("code", c.getCode());
            metadata.put("language", c.getLanguage());
        }

        // Add the node into the flow and if it succeed send a message back to the connected clients
        // & start a kernel if the node is a Processing or Simulation
        if(flow.addNode(id, component, metadata, graph, executable)) {
            // Answer
            sendAddNodeMessage(id, graph);
            sendAddInportAndOutportForNode(flow.getNode(id, graph), graph);

        } else {
            sendError("Unable to create node because graph " + graph + " doesn't exist");
        }
    }

    /**
     * Handle a "removenode" message by removing the Node object with the given id, from the Flow object.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removenode
     *
     * @param payload {JSONObject} the payload from the received message
     */
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
            sendError("Unable to remove node because graph " + graph + " doesn't exist");
        }
    }

    /**
     * Handle a "renamenode" message by changing its id.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renamenode
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void renamenode (JSONObject payload) {
        // Retrieve needed data for renameNode() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        if (flow.renameNode(from, to, graph)) {
            // Answer
            sendRenameNodeMessage(from, to, graph);
        } else {
            sendError("Unable to rename node " + from);
        }

    }

    /**
     * Handle a "changenode" message by updating the metadata field of the Node object with the given id,
     * from the Flow object.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-changenode
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void changenode (JSONObject payload) {
        // Retrieve needed data for changeNode() method
        String id = (String) payload.get("id");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.changeNode(id, metadata, graph)) {
            // Answer
            sendChangeNodeMessage(id, graph);
        } else {
            sendError("Unable to change node " + id);
        }
    }

    /**
     * Handle a "addedge" message by adding a new Edge object into the Flow. The edge is created from the data
     * we retrieve in the given payload object, in accordance to the FBPNP documentation.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addedge
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void addedge (JSONObject payload) {
        // Retrieve needed data for addEdge() method
        String id = (String) payload.get("id");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.addEdge(id, src, tgt, metadata, graph)) {
            // Answer
            sendAddEdgeMessage(src, tgt, graph);
        } else {
            String srcNodeId = (String) src.get("node");
            String tgtNodeId = (String) src.get("node");
            System.err.println("[ERROR] Cannot create graph for src : " + srcNodeId + ", target : " + tgtNodeId + ", graph : " + graph + " because one of them doesn't exist");
        }

    }

    /**
     * Handle a "removeedge" message by removing the Edge object with the given id, from the Flow object.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeedge
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void removeedge (JSONObject payload) {
        // Retrieve needed data for removeEdge() method
        String id = (String) payload.get("id");
        String graph = (String) payload.get("graph");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");

        if (flow.removeEdge(id, graph, src, tgt)) {
            // Answer
            sendRemoveEdgeMessage(id, graph, src, tgt);
        } else {
            sendError("Unable to remove edge. Maybe the graph doesn't exist or the edge has already been removed.");
        }

    }

    /**
     * Handle a "changeedge" message by updating the metadata field of the Edge object with the given id,
     * from the Flow object.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-changeedge
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void changeedge (JSONObject payload) {
        // Retrieve needed data for changeEdge() method
        String id = (String) payload.get("id");
        String graph = (String) payload.get("graph");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");

        if (flow.changeEdge(id, graph, metadata, src, tgt)) {
            // Answer
            sendChangeEdgeMessage(graph, src, tgt);
        } else {
            sendError("Unable to change request edge");
        }

    }

    /**
     * Handle a "addinitial" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addinitial
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void addinitial (JSONObject payload) {
        // Retrieve needed data for addInitial() method
        String graph = (String) payload.get("graph");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");

        flow.addInitial(graph, metadata, src, tgt);

        // answer later

    }

    /**
     * Handle a "removeinitial" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeinitial
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void removeinitial (JSONObject payload) {
        // Retrieve needed data for removeInitial() method
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");
        String graph = (String) payload.get("graph");

        flow.removeInitial(graph, src, tgt);

        // answer later
    }

    /**
     * Handle a "addinport" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addinport
     *
     * @param payload {JSONObject} the payload from the received message
     */
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

    /**
     * Handle a "removeinport" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeinport
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void removeinport (JSONObject payload) {
        // Retrieve needed data for removeInport() method
        String name = (String) payload.get("public");
        String graph = (String) payload.get("graph");

        flow.removeInport(name, graph);

        // answer later
    }

    /**
     * Handle a "renameinport" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renameinport
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void renameinport (JSONObject payload) {
        // Retrieve needed data for renameInport() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        flow.renameInport(from, to, graph);

        // answer later
    }

    /**
     * Handle a "addoutport" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addoutport
     *
     * @param payload {JSONObject} the payload from the received message
     */
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

    /**
     * Handle a "removeoutport" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeoutport
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void removeoutport (JSONObject payload) {
        // Retrieve needed data for removeOutport() method
        String name = (String) payload.get("public");
        String graph = (String) payload.get("graph");

        flow.removeOutport(name, graph);

        // answer later
    }

    /**
     * Handle a "renameoutport" message. Don't do anything for now.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renameoutport
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void renameoutport (JSONObject payload) {
        // Retrieve needed data for renameOutport() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        flow.renameOutport(from, to, graph);

        // answer later
    }

    /**
     * Handle a "addgroup" message by adding a group to the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addgroup
     *
     * @param payload {JSONObject} the payload from the received message
     */
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
            sendError("Unable to add group to graph " + graph + " because it doesn't exist");
        }
    }

    /**
     * Handle a "removegroup" message by removing a group from the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removegroup
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void removegroup (JSONObject payload) {
        // Retrieve needed data for removeGroup() method
        String name = (String) payload.get("name");
        String graph = (String) payload.get("graph");

        if (flow.removeGroup(name, graph)) {
            // Answer
            sendRemoveGroupMessage(name, graph);
        } else {
            sendError("Unable to remove group " + name + " because it doesn't exist or graph " + graph + " doesn't exist");
        }
    }

    /**
     * Handle a "renamegroup" message by renaming an existing group from the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renamegroup
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void renamegroup (JSONObject payload) {
        // Retrieve needed data for renameGroup() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        if (flow.renameGroup(from, to, graph)) {
            // Answer
            sendRenameGroupMessage(from, to, graph);
        } else {
            sendError("Unable to rename group " + from);
        }
    }

    /**
     * Handle a "changegroup" message by updating its metadata.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-changegroup
     *
     * @param payload {JSONObject} the payload from the received message
     */
    private void changegroup (JSONObject payload) {
        // Retrieve needed data for changeGroup() method
        String name = (String) payload.get("name");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.changeGroup(name, metadata, graph)) {
            // Answer
            sendChangeGroupMessage(name, graph);
        } else {
            sendError("Unable to change group " + name + "'s metadata");
        }
    }

    /* =================================================================================================================
                                            PRIVATE METHODS TO SEND MESSAGES
       ===============================================================================================================*/

    /**
     * Send a "clear" message on the graph protocol
     *
     * https://flowbased.github.io/fbp-protocol/#graph-clear
     */
    private void sendClearMessage () {
        System.out.println("You are trying to empty the graph. It is too dangerous to be implemented");
    }

    /**
     * Send a "addnode" message for the Node with the given id in the given graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addnode
     *
     * @param id {String} id of the new node.
     * @param graph {String} id of the graph on which to create the node.
     */
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

    /**
     * Send a "removenode" message in order to remove the node with the given id from the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removenode
     *
     * @param id {String} id of the node to remove.
     * @param graph {String} id of the graph on which to remove the node.
     */
    private void sendRemoveNodeMessage (String id, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", id);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("removenode", payload);
    }

    /**
     * Send a "renamenode" message in order to replace the node with the new given id.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renamenode
     *
     * @param from {String} previous id
     * @param to {String} new id
     * @param graph {String} id of the graph the node is on
     */
    private void sendRenameNodeMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("renamenode", payload);
    }

    /**
     * Send a "changenode" message in order to update its metadata.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-changenode
     *
     * @param id {String} the id of the node
     * @param graph {String} the graph the node is on
     */
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

    /**
     * Send a "addedge" message in order to create a new edge that connects two existing nodes.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addedge
     *
     * @param src {JSONObject} the node's id, port and index of the source node
     * @param tgt {JSONObject} the node's id, port and index of the target node
     * @param graph {String} the graph the edge will be on
     */
    private void sendAddEdgeMessage (JSONObject src, JSONObject tgt, String graph) {
        Edge edge = flow.getEdge(src, tgt, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", edge.getId());
        payload.put("src", edge.getSrc());
        payload.put("tgt", edge.getTgt());
        payload.put("metadata", edge.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("addedge", payload);
    }

    /**
     * Send a "removeedge" message in order to remove the edge from the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeedge
     *
     * @param id {String} the id of the edge
     * @param graph {String} the id of the graph the edge is on
     * @param src {JSONObject} the node's id, port and index of the source node
     * @param tgt {JSONObject} the node's id, port and index of the target node
     */
    private void sendRemoveEdgeMessage (String id, String graph, JSONObject src, JSONObject tgt) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", id);
        payload.put("graph", graph);
        payload.put("src", src);
        payload.put("tgt", tgt);

        // Send the message
        sendMessageToAll("removeedge", payload);
    }

    /**
     * Send a "changeedge" message in order to connect an edge update its metadata.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-changeedge
     *
     * @param graph {String} the id of the graph the edge is on
     * @param src {JSONObject} the node's id, port and index of the source node
     * @param tgt {JSONObject} the node's id, port and index of the target node
     */
    private void sendChangeEdgeMessage (String graph, JSONObject src, JSONObject tgt) {
        Edge edge = flow.getEdge(src, tgt, graph);
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", edge.getId());
        payload.put("src", edge.getSrc());
        payload.put("tgt", edge.getTgt());
        payload.put("metadata", edge.getMetadata());
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("changeedge", payload);
    }

    /**
     * Send a "addinitial" message. Behavior and interest to find ...
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addinitial
     *
     * @param msg {JSONObject} the msg to send
     */
    private void sendAddInitialMessage (JSONObject msg) {
        // Not used for now
    }

    /**
     * Send a "removeinitial" message. Behavior and interest to find ...
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeinitial
     *
     * @param msg {JSONObject} the msg to send
     */
    private void sendRemoveInitialMessage (JSONObject msg) {
        // Not used for now
    }

    /**
     * Send a "addinport" message.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addinport
     *
     * @param port {Port} the port to add
     * @param node {String} the id of the node on which to add the port
     * @param graph {String} the id of the graph the node is on
     */
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

    /**
     * Send a "removeinport" message.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeinport
     *
     * @param name {String} the name of the port to delete
     * @param graph {String} the id of the graph the port is on
     */
    private void sendRemoveInportMessage (String name, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("public", name);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("removeinport", payload);
    }

    /**
     * Send a "renameinport" message.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renameinport
     * @param from {String} original exported port name
     * @param to {String} new exported port name
     * @param graph {String} graph the action targets
     */
    private void sendRenameInportMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("renameinport", payload);
    }

    /**
     * Send a "addoutport" message.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addoutport
     *
     * @param port {Port} the new port
     * @param node {String} the node's id
     * @param graph {String} the id of the graph the action targets
     */
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

    /**
     * Send a "removeoutport" message. Remove an exported port in the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removeoutport
     *
     * @param name {String} name of the exported port
     * @param graph {String} id of the graph the action targets
     */
    private void sendRemoveOutportMessage (String name, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("public", name);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("removeoutport", payload);
    }

    /**
     * Send a "renameoutport" message. Rename an exported port in the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renameoutport
     *
     * @param from {String} previous name
     * @param to {String} new name
     * @param graph {String} id of the graph the action targets
     */
    private void sendRenameOutportMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message to all connected clients
        sendMessageToAll("renameoutport", payload);
    }

    /**
     * Send a "addgroup" message. Add a group to the graph
     *
     * https://flowbased.github.io/fbp-protocol/#graph-addgroup
     *
     * @param name {String} name of the group
     * @param graph {String} id of the graph the action targets
     */
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

    /**
     * Send a "removegroup" message. Remove a group from the graph
     *
     * https://flowbased.github.io/fbp-protocol/#graph-removegroup
     *
     * @param name {String} name of the group
     * @param graph {String} id of the graph the action targets
     */
    private void sendRemoveGroupMessage (String name, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("name", name);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("removegroup", payload);
    }

    /**
     * Send a "removegroup" message. Rename a group in the graph
     *
     * https://flowbased.github.io/fbp-protocol/#graph-renamegroup
     *
     * @param from {String} the previous name
     * @param to {String} the new name
     * @param graph {String} id of the graph the action targets
     */
    private void sendRenameGroupMessage (String from, String to, String graph) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("from", from);
        payload.put("to", to);
        payload.put("graph", graph);

        // Send the message
        sendMessageToAll("renamegroup", payload);
    }

    /**
     * Send a "changegroup" message. Change a group's metadata.
     *
     * https://flowbased.github.io/fbp-protocol/#graph-changegroup
     *
     * @param name {String} name of the group
     * @param graph {String} id of the graph the action targets
     */
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
                      PRIVATE METHODS TO SEND MESSAGES. METHODS THAT ARE SPECIFIC TO THIS PROGRAM
       ===============================================================================================================*/

    /**
     * Send addinport and addoutport message for the given node.
     *
     * @param node {Node}
     * @param graph {String} the id of the graph the action targets
     */
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

