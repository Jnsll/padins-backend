package FBPNetworkProtocol;

import Core.Workspace;
import org.json.simple.JSONObject;
import Flow.Flow;

/**
 * Created by antoine on 26/05/2017.
 */
public class GraphMessageHandler implements FBPProtocolHandler {

    // Attributes
    FBPNetworkProtocolManager owningManager;
    Workspace owningWorkspace;
    Flow flow;
    final String PROTOCOL = "graph";

    // Constructor
    public GraphMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
        this.flow = owningManager.owningWorkspace.getFlow();
        this.owningWorkspace = manager.owningWorkspace;
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
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if(flow.addNode(id, component, metadata, graph)) {
            // answer
        } else {

        }
    }

    private void removenode (JSONObject payload) {
        // Retrieve needed data for removeNode() method
        String id = (String) payload.get("id");
        String graph = (String) payload.get("graph");

        if (flow.removeNode(id, graph)) {
            // Answer
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to create node because graph " + graph + " doesn't exist");
        }
    }

    private void renamenode (JSONObject payload) {
        // Retrieve needed data for renameNode() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        if (flow.renameNode(from, to, graph)) {
            // Answer
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
            // answer
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
            // answer
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

    }

    private void removeinitial (JSONObject payload) {
        // Retrieve needed data for removeInitial() method
        JSONObject src = (JSONObject) payload.get("src");
        JSONObject tgt = (JSONObject) payload.get("tgt");
        String graph = (String) payload.get("graph");

        flow.removeInitial(graph, src, tgt);
    }

    private void addinport (JSONObject payload) {
        // Retrieve needed data for addInport() method
        String name = (String) payload.get("public");
        String node = (String) payload.get("node");
        String port = (String) payload.get("port");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        flow.addInport(name, node, port, metadata, graph);
    }

    private void removeinport (JSONObject payload) {
        // Retrieve needed data for removeInport() method
        String name = (String) payload.get("public");
        String graph = (String) payload.get("graph");

        flow.removeInport(name, graph);
    }

    private void renameinport (JSONObject payload) {
        // Retrieve needed data for renameInport() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        flow.renameInport(from, to, graph);
    }

    private void addoutport (JSONObject payload) {
        // Retrieve needed data for addOutport() method
        String name = (String) payload.get("public");
        String node = (String) payload.get("node");
        String port = (String) payload.get("port");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        flow.addOutport(name, node, port, metadata, graph);

    }

    private void removeoutport (JSONObject payload) {
        // Retrieve needed data for removeOutport() method
        String name = (String) payload.get("public");
        String graph = (String) payload.get("graph");

        flow.removeOutport(name, graph);
    }

    private void renameoutport (JSONObject payload) {
        // Retrieve needed data for renameOutport() method
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String graph = (String) payload.get("graph");

        flow.renameOutport(from, to, graph);
    }

    private void addgroup (JSONObject payload) {
        // Retrieve needed data for addGroup() method
        String name = (String) payload.get("name");
        JSONObject nodes = (JSONObject) payload.get("nodes");
        JSONObject metadata = (JSONObject) payload.get("metadata");
        String graph = (String) payload.get("graph");

        if (flow.addGroup(name, nodes, metadata, graph)) {
            // answer
        } else {
            owningWorkspace.getClientCommunicationManager().sendError("graph", "Unable to add group to graph " + graph + " because it doesn't exist");
        }
    }

    private void removegroup (JSONObject payload) {
        // Retrieve needed data for removeGroup() method
        String name = (String) payload.get("name");
        String graph = (String) payload.get("graph");

        if (flow.removeGroup(name, graph)) {
            // answer
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
            // answer
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
            // answer
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

    private void sendAddNodeMessage (JSONObject payload) {

    }

    private void sendRemoveNodeMessage (JSONObject payload) {

    }

    private void sendRenameNodeMessage (JSONObject payload) {

    }

    private void sendChangeNodeMessage (JSONObject payload) {

    }

    private void sendAddEdgeMessage (JSONObject payload) {

    }

    private void sendRemoveEdgeMessage (JSONObject payload) {

    }

    private void sendChangeEdgeMessage (JSONObject payload) {

    }

    private void sendAddInitialMessage (JSONObject payload) {

    }

    private void sendRemoveInitialMessage (JSONObject payload) {

    }

    private void sendAddInportMessage (JSONObject payload) {

    }

    private void sendRemoveInportMessage (JSONObject payload) {

    }

    private void sendRenameInportMessage (JSONObject payload) {

    }

    private void sendAddOutportMessage (JSONObject payload) {

    }

    private void sendRemoveOutportMessage (JSONObject payload) {

    }

    private void sendRenameOutportMessage (JSONObject payload) {

    }

    private void sendAddGroupMessage (JSONObject payload) {

    }

    private void sendRemoveGroupMessage (JSONObject payload) {

    }

    private void sendRenameGroupMessage (JSONObject payload) {

    }

    private void sendChangeGroupMessage (JSONObject payload) {

    }

}

