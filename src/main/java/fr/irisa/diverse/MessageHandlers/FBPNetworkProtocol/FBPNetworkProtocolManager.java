package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.Webserver.Servlets.WebsocketOthers.ServerSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import fr.irisa.diverse.Flow.*;
import java.util.ArrayList;

/**
 * The FBPNetworkProtocolManager handle the FBP Network Protocol's compliant messages received on the socket endpoint.
 *
 * Its main role is to read the protocol field of the message and redirect the message to the proper handler, and to
 * send the already formatted messages to the clients.
 *
 * It can also send a few custom messages (startnode and finishnode), handles the traceback received from the kernel
 * and send a changenode message.
 *
 * Created by antoine on 26/05/2017.
 */
@SuppressWarnings("unchecked")
public class FBPNetworkProtocolManager {

    // TODO : implement capabilities as described here : https://flowbased.github.io/fbp-protocol/#capabilities

    /* =================================================================================================================
                                                  ATTRIBUTES
       ===============================================================================================================*/

    // Protocol specific handlers
    private NetworkMessageHandler network = null;
    private GraphMessageHandler graph = null;
    private ComponentMessageHandler component = null;
    private RuntimeMessageHandler runtime = null;
    private TraceMessageHandler trace = null;

    // Linked classes
    private ServerSocket owningSocket = null;
    Workspace owningWorkspace = null;

    // Information about the supported fbp network protocol
    final String FBP_NETWORK_PROTOCOL_VERSION = "0.6";

    // Information specific to the workspace
    private String componentsLibrary = "";

    /* =================================================================================================================
                                                  CONSTRUCTOR
       ===============================================================================================================*/

    public FBPNetworkProtocolManager (Workspace workspace) {
        this.owningWorkspace = workspace;
        componentsLibrary = workspace.getLibrary();

        network = new NetworkMessageHandler(this);
        graph = new GraphMessageHandler(this);
        component = new ComponentMessageHandler(this);
        runtime = new RuntimeMessageHandler(this);
        trace = new TraceMessageHandler(this);

    }

    /* =================================================================================================================
                                                  GETTERS AND SETTERS
       ===============================================================================================================*/

    /**
     * Set the attached socket. The socket will be used to send messages to the right client.
     *
     * @param socket {ServerSocket} the connected client's socket instance
     */
    public void setSocket (ServerSocket socket) {
        owningSocket = socket;
    }

    /**
     * Returns the name of the component library used in the workspace.
     *
     * @return {String} the name of the component library
     */
    String getComponentsLibrary() {
        return componentsLibrary;
    }

    /* =================================================================================================================
                                       MessageHandler.Whole INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

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
                System.err.println("Received message for an unknown protocol : " + message.toJSONString());
                break;
        }


    }

    /* =================================================================================================================
                                                  PUBLIC METHODS
       ===============================================================================================================*/


    /**
     * Send the given message to the connected client.
     *
     * @param msg {FBPMessage} the message to send
     */
    synchronized public void send (FBPMessage msg) {
        sendMsgToSocket(msg, owningSocket);
    }

    /**
     * Send the given message through the given socket.
     *
     * @param msg {FBPMessage} the message to send
     * @param socket {ServerSocket} the destination socket
     */
    synchronized private void sendMsgToSocket (FBPMessage msg, ServerSocket socket) {
        // TODO : add secret handling
        if (socket != null) {
            socket.send(msg.toJSONString());
        }
    }

    /**
     * Send the given message to all the clients connected on the workspace.
     *
     * @param msg {FBPMessage} the message to send
     */
    synchronized void sendToAll (FBPMessage msg) {
        // Retrieve all the clients connected to the workspace
        ArrayList<ServerSocket> clients = new ArrayList<>();
        clients.addAll(owningWorkspace.getConnectedClients());

        // Send the message to each client
        for (ServerSocket client : clients) {
            sendMsgToSocket(msg, client);
        }
    }

    /**
     * Send an error message to the client.
     *
     * @param protocol {String} the protocol on which the error has been thrown.
     * @param error {String} the error message.
     */
    synchronized public void sendError(String protocol, String error) {
        FBPMessage msg = createErrorMessage(protocol, error);

        send(msg);
    }

    /**
     * Send an error message to all the clients connected on the workspace.
     * @param protocol {String} the protocol on which the error has been thrown.
     * @param error {String} the error message.
     */
    synchronized public void sendErrorToAll(String protocol, String error) {
        FBPMessage msg = createErrorMessage(protocol, error);

        sendToAll(msg);
    }

    /**
     * Send a changenode message to all clients in order to tell the UIs to update the node.
     *
     * @param node {Node} the updated node
     */
    public void sendUpdateNodeMessage (Node node) {
        JSONObject payload = new JSONObject();
        payload.put("id", node.getId());
        payload.put("metadata", node.getMetadata());
        payload.put("graph", node.getGraph());

        FBPMessage msg = new FBPMessage("graph", "changenode", payload.toJSONString());

        sendToAll(msg);
    }

    /**
     * Handle the traceback coming from the kernel, redirecting it to the UIs.
     *
     * @param traceback {JSONArray} the traceback, as an array on text lines.
     * @param k {Kernel} the kernel that provide the traceback
     */
    public void handleTracebackFromKernel (JSONArray traceback, Kernel k) {
        JSONObject payload = new JSONObject();
        payload.put("node", owningWorkspace.getNodeIdForKernel(k));
        payload.put("traceback", traceback);

        FBPMessage msg = new FBPMessage("trace", "nodetraceback", payload.toJSONString());

        sendToAll(msg);
    }

    /**
     * Create an FBPNP compliant error message from the given protocol and error message.
     *
     * @param protocol {String} the protocol on which the error has been thrown.
     * @param error {String} the error message.
     * @return {FBPMessage} the FBPNP compliant error message.
     */
    private FBPMessage createErrorMessage (String protocol, String error) {
        JSONObject obj = new JSONObject();
        obj.put("message", error);
        String payload = obj.toJSONString();

        return new FBPMessage(protocol, "error", payload);
    }

    /* =================================================================================================================
                                       MESSAGE ADDED TO FIT OUR NEEDS
       ===============================================================================================================*/

    /**
     * Send a startnode message that say that the node with the given id has just started being executed.
     *
     * @param id {String} the uuid of the node
     */
    public void sendStartNode (String id) {
        JSONObject payload = new JSONObject();
        payload.put("id", id);

        FBPMessage msg = new FBPMessage("network", "startnode", payload.toJSONString());

        // Send it
        sendToAll(msg);
    }

    /**
     * Send a stopnode message that say that the node with the given id has just stopped being executed.
     *
     * @param id {String} the uuid of the node
     */
    public void sendFinishNode (String id) {
        JSONObject payload = new JSONObject();
        payload.put("id", id);

        FBPMessage msg = new FBPMessage("network", "finishnode", payload.toJSONString());

        sendToAll(msg);
    }
}
