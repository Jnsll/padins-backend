package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

import fr.irisa.diverse.Utils.JSON;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * A very simple class to create and handle FBP Network Protocol compliant messages.
 * The structure of a message is described here :
 * https://flowbased.github.io/fbp-protocol/#message-structure
 *
 * Created by antoine on 26/05/2017.
 */
@SuppressWarnings("unchecked")
public class FBPMessage {

    // Attributes
    private JSONObject message = null;

    /* =================================================================================================================
                                                        CONSTRUCTORS
       ===============================================================================================================*/

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

        // TODO : verification steps
    }

    /* =================================================================================================================
                                              GETTERS AND SETTERS
       ===============================================================================================================*/

    /**
     * Returns the protocol field of the message as a String.
     * The protocols are : runtime - graph - component - network - trace
     *
     * @return {String} the protocol field of the message.
     */
    public String getProtocol () {
        return (String) message.get("protocol");
    }

    /**
     * Returns the command field of the message.
     * The command is the name of the action to do. For example removeedge
     *
     * @return {String} the command field of the message
     */
    public String getCommand () {
        return (String) message.get("command");
    }

    /**
     * Returns the payload of the message, as a JSONObject.
     * The payload contains all the interesting information to handle the message. It must be compliant
     * with the documentation. Each command has a different payload.
     *
     * @return {JSONObject} the payload of the message
     */
    public JSONObject getPayload() {
        return (JSONObject) message.get("payload");
    }

    /**
     * Set the protocol field of the message.
     *
     * @param protocol {String} the new protocol. Must be : runtime - graph - component - network - trace
     */
    public void setProtocol(String protocol) {
        message.put("protocol", protocol);
    }

    /**
     * Set the command field of the message.
     *
     * @param command {String} the new command
     */
    public void setCommand (String command) {
        message.put("command", command);
    }

    /**
     * Set the payload of the message, from a String.
     *
     * @param payload {String} the new payload, as a serialized JSON object
     */
    public void setPayload (String payload) {
        // Parse payload to avoid backslash in the serialized message
        JSONParser parser = new JSONParser();
        JSONObject p = new JSONObject();
        try {
            p = (JSONObject) parser.parse(payload);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        message.put("payload", p);
    }

    /**
     * Serialize the message to a JSON compliant String.
     * Commonly used to send the message through a socket.
     *
     * @return {String} the serialized message
     */
    public String toJSONString () { return message.toJSONString(); }
}
