package FBPNetworkProtocol;

import org.json.simple.JSONObject;

/**
 * Created by antoine on 30/05/17.
 */
public abstract class SendMessageOverFBP {

    FBPNetworkProtocolManager owningManager;
    String PROTOCOL = "";

    protected void sendMessage (String command, JSONObject payload) {
        // Build FBPMessage to send
        FBPMessage message = new FBPMessage(PROTOCOL, command, payload.toJSONString());

        owningManager.send(message);

    }

    protected void sendMessageToAll (String command, JSONObject payload) {
        // Build FBPMessage to send
        FBPMessage message = new FBPMessage(PROTOCOL, command, payload.toJSONString());

        owningManager.sendToAll(message);
    }
}
