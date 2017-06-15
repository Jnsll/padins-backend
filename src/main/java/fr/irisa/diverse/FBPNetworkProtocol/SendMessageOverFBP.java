package fr.irisa.diverse.FBPNetworkProtocol;

import org.json.simple.JSONObject;

/**
 * Abstract class that implements methods related to sending messages from the server to the UIs.
 *
 * Created by antoine on 30/05/17.
 */
abstract class SendMessageOverFBP {

    FBPNetworkProtocolManager owningManager;
    String PROTOCOL = "";

    /**
     * Send a message to only one client on the UI, the same as the one that sent a request.
     *
     * @param command the type of the message to send
     * @param payload the interesting content of the message
     */
    void sendMessage(String command, JSONObject payload) {
        // Build FBPMessage to send
        FBPMessage message = new FBPMessage(PROTOCOL, command, payload.toJSONString());

        owningManager.send(message);

    }

    /**
     * Send a message to all the clients connected to the workspace/project.
     *
     * @param command the type of the message to send
     * @param payload the interesting content of the message
     */
    void sendMessageToAll(String command, JSONObject payload) {
        // Build FBPMessage to send
        FBPMessage message = new FBPMessage(PROTOCOL, command, payload.toJSONString());

        owningManager.sendToAll(message);
    }

    /**
     * Send an error message to only one client on the UI, the same as the one that sent a request.
     *
     * @param message The text of the error to send
     */
    void sendError (String message) {
        owningManager.sendError(PROTOCOL, message);
    }

    /**
     * Send an error message to all the clients connected to the workspace/project.
     *
     * @param message The text of the error to send
     */
    void sendErrorToAll (String message) { owningManager.sendErrorToAll(PROTOCOL, message);}
}
