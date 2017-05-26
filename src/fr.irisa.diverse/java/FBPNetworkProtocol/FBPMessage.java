package FBPNetworkProtocol;

import Utils.JSON;
import org.json.simple.JSONObject;

/**
 * Created by antoine on 26/05/2017.
 */
public class FBPMessage {

    // Attributes
    JSONObject message = null;

    public FBPMessage () {
        message = new JSONObject();
    }

    public FBPMessage (String protocol, String command, String payload) {
        this();

        setProtocol(protocol);
        setCommand(command);
        message.put("payload", payload);
    }

    public FBPMessage (String message) {
        this.message = JSON.stringToJsonObject(message);

        // TODO verification steps
    }

    // Getters and setters

    public String getProtocol () {
        return (String) message.get("protocol");
    }

    public String getCommand () {
        return (String) message.get("command");
    }

    public JSONObject getPayloadAsJSON () {
        return JSON.stringToJsonObject((String) message.get("payload"));
    }

    public String getPayloadAsJString () {
        return (String) message.get("payload");
    }

    public void setProtocol(String protocol) {
        message.put("protocol", protocol);
    }

    public void setCommand (String command) {
        message.put("command", command);
    }

    public void setPayload (String payload) {
        message.put("payload", payload);
    }

    public void setPayloadFromJson (JSONObject payload) {
        message.put("payload", payload.toString());
    }

    public String toJSONString () { return message.toJSONString(); }
}
