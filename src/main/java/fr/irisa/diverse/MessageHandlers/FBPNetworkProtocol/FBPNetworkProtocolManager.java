package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.Webserver.Servlets.WebsocketOthers.ServerSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import fr.irisa.diverse.Flow.*;

import javax.websocket.MessageHandler;
import java.util.ArrayList;

/**
 * Created by antoine on 26/05/2017.
 */
public class FBPNetworkProtocolManager implements MessageHandler.Whole<FBPMessage> {

    // TODO : implement capabilities as described here : https://flowbased.github.io/fbp-protocol/#capabilities

    // Attributes
    private NetworkMessageHandler network = null;
    private GraphMessageHandler graph = null;
    private ComponentMessageHandler component = null;
    private RuntimeMessageHandler runtime = null;
    private TraceMessageHandler trace = null;
    private ServerSocket owningSocket = null;
    Workspace owningWorkspace = null;
    final String FBP_NETWORK_PROTOCOL_VERSION = "0.6";
    private Flow flow = null;

    private String componentsLibrary = "";

    // Constructor
    public FBPNetworkProtocolManager (Workspace workspace) {
        this.owningWorkspace = workspace;
        componentsLibrary = workspace.getLibrary();

        network = new NetworkMessageHandler(this);
        graph = new GraphMessageHandler(this);
        component = new ComponentMessageHandler(this);
        runtime = new RuntimeMessageHandler(this);
        trace = new TraceMessageHandler(this);

        flow = workspace.getFlow();

    }

    /* =================================================================================================================
                                                  GETTERS AND SETTERS
       ===============================================================================================================*/

    public void setSocket (ServerSocket socket) {
        owningSocket = socket;
    }

    public String getComponentsLibrary() {
        return componentsLibrary;
    }

    /* =================================================================================================================
                                       MessageHandler.Whole INTERFACE METHOD IMPLEMENTATION
           ===============================================================================================================*/
    @Override
    public void onMessage(FBPMessage message) {

        String protocol = message.getProtocol();

        // Redirect message to proper handler
        switch (protocol) {
            case "runtime" :
                runtime.handleMessage(message);
                break;
            case "graph" :
                graph.handleMessage(message);
                break;
            case "component" :
                component.handleMessage(message);
                break;
            case "network" :
                network.handleMessage(message);
                break;
            case "trace" :
                trace.handleMessage(message);
                break;
            default :
                System.err.println("Received message for unknown protocol : " + message.toJSONString());
                break;
        }


    }

    /* =================================================================================================================
                                                  PUBLIC METHODS
       ===============================================================================================================*/


    public void send (FBPMessage msg) {
        // TODO : add secret handling
        if (owningSocket != null) {
            owningSocket.send(msg.toJSONString());
        }
    }

    public void sendToAll (FBPMessage msg) {
        ArrayList<ServerSocket> clients = owningWorkspace.getConnectedClients();

        for (ServerSocket client : clients) {
            // TODO : add secret handling for each client
            client.send(msg.toJSONString());
        }
    }

    public void sendError(String protocol, String error) {
        FBPMessage msg = createErrorMessage(protocol, error);

        send(msg);
    }

    public void sendErrorToAll(String protocol, String error) {
        FBPMessage msg = createErrorMessage(protocol, error);

        sendToAll(msg);
    }

    public void sendUpdateNodeMessage (Node node) {
        JSONObject payload = new JSONObject();
        payload.put("id", node.getId());
        payload.put("metadata", node.getMetadata());
        payload.put("graph", node.getGraph());

        FBPMessage msg = new FBPMessage("graph", "changenode", payload.toJSONString());

        sendToAll(msg);
    }

    public void handleTracebackFromKernel (JSONArray traceback, Kernel k) {
        JSONObject payload = new JSONObject();
        payload.put("node", owningWorkspace.getNodeIdForKernel(k));
        payload.put("traceback", traceback);

        FBPMessage msg = new FBPMessage("trace", "nodetraceback", payload.toJSONString());

        sendToAll(msg);
    }

    private FBPMessage createErrorMessage (String protocol, String error) {
        JSONObject obj = new JSONObject();
        obj.put("message", error);
        String payload = obj.toJSONString();

        FBPMessage msg = new FBPMessage(protocol, "error", payload);

        return msg;
    }

    public void sendNodeUpdate (Node node) {
        graph.sendChangeNodeMessage(node.getId(), node.getGraph());
    }
}
