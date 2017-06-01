package fr.irisa.diverse.FBPNetworkProtocol;

import org.json.simple.JSONObject;

/**
 * Created by antoine on 30/05/17.
 */
abstract class SendMessageOverFBP {

    FBPNetworkProtocolManager owningManager;
    String PROTOCOL = "";

    void sendMessage(String command, JSONObject payload) {
        // Build FBPMessage to send
        FBPMessage message = new FBPMessage(PROTOCOL, command, payload.toJSONString());

        //owningManager.send(message);

    }

    void sendMessageToAll(String command, JSONObject payload) {
        // Build FBPMessage to send
        FBPMessage message = new FBPMessage(PROTOCOL, command, payload.toJSONString());

        //owningManager.sendToAll(message);
    }
}
