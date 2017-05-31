package fr.irisa.diverse.FBPNetworkProtocol;

import fr.irisa.diverse.Utils.JSON;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Created by antoine on 26/05/2017.
 */
class FBPMessage {

    // Attributes
    private JSONObject message = null;

    public FBPMessage () {
        message = new JSONObject();
    }

    public FBPMessage (String protocol, String command, String payload) {
        this();

        setProtocol(protocol);
        setCommand(command);
        setPayload(payload);
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
        return (JSONObject) message.get("payload");
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
        // Parse payload to avoid backslash in the serialized message
        JSONParser parser = new JSONParser();
        JSONObject p = new JSONObject();
        try {
            p = (JSONObject) parser.parse(payload);
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        message.put("payload", p);
    }

    public void setPayloadFromJson (JSONObject payload) {
        message.put("payload", payload.toString());
    }

    public String toJSONString () { return message.toJSONString(); }
}
