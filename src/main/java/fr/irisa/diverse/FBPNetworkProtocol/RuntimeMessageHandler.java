package fr.irisa.diverse.FBPNetworkProtocol;

import org.json.simple.JSONObject;

/**
 * Class managing the Runtime Message for the Flow-Based Programming Network Protocol
 * To know more about this protocol, take a look at the doc on J.Paul Morisson great website :
 * https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
public class RuntimeMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    private FBPNetworkProtocolManager owningManager;

    // Constructor
    public RuntimeMessageHandler (FBPNetworkProtocolManager manager) {
        this.PROTOCOL = "runtime";
        this.owningManager = manager;
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "getruntime" :
                getruntime();
                break;
            case "packet" :
                packet();
                break;
            default :
                System.err.println("Received message for unknown protocol : " + message.toJSONString());
                sendError("Error with message : " + message.toJSONString());
                break;
        }

    }

    /* =================================================================================================================
                                        PRIVATE METHODS TO HANDLE RECEIVED MESSAGES
       ===============================================================================================================*/

    private void getruntime () {
        sendRuntimeMessage();
    }

    private void packet () {
        // TODO : figure out int what case we could use it and how.
    }

    /* =================================================================================================================
                                               PRIVATE METHODS TO SEND MESSAGES
       ===============================================================================================================*/

    private void sendPacketMessage (String port, String event, String graph, JSONObject payloadToSend) {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("port", port);
        payload.put("event", event);
        payload.put("graph", graph);
        if (payloadToSend == null ) payload.put("payload", "") ;
        else payload.put("payload", payloadToSend.toJSONString());

        // Send it
        sendMessage("packet", payload);
    }

    /**
     * Send a ports message as a response to packet or each time the available ports change
     * as described here : https://flowbased.github.io/fbp-protocol/#runtime-ports
     */
    private void sendPortsMessage () {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("graph", owningManager.owningWorkspace.getUuid());
        payload.put("inPorts", "[]"); // TODO : try to understand and implement it (same with below)
        payload.put("outPorts", "[]");

        // Send it
        sendMessage("ports", payload);
    }

    /**
     * Send a runtime message as a response as described here : https://flowbased.github.io/fbp-protocol/#runtime-runtime
     */
    private void sendRuntimeMessage () {
        // Build payload
        JSONObject payload = new JSONObject();
        payload.put("id", owningManager.owningWorkspace.getUuid());
        payload.put("label", owningManager.owningWorkspace.getName());
        payload.put("version", owningManager.FBP_NETWORK_PROTOCOL_VERSION);
        payload.put("allCapabilities", ""); // TODO : implement capabilities listing feature (look at doc)
        payload.put("capabilities", ""); // TODO : implement capabilities listing feature (look at doc)
        payload.put("graph", owningManager.owningWorkspace.getFlow().getId());
        payload.put("type", owningManager.owningWorkspace.RUNTIME_TYPE);
        payload.put("namespace", owningManager.getComponentsLibrary());
        payload.put("repository", ""); // TODO : implement repository feature and below one
        payload.put("repositoryVersion", "0.0.0");

        // Send it
        sendMessage("runtime", payload);
    }



}
