package FBPNetworkProtocol;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A FBPProtocolHandler is a component that handle message on a specific FBP Network Protocol sub-protocol.
 * To know more about FBPNP sub-protocols : https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
public interface FBPProtocolHandler {

    /** Handle a received message
     *
     * @param message : the message to handle
     */
    void handleMessage(JSONObject message);

    /** Retrieve and return the content of a received message
     *
     * @param message
     * @return
     */
    static JSONObject getContent(JSONObject message) {
        JSONParser parser = new JSONParser();
        JSONObject content = new JSONObject();
        try {
            Object o = parser.parse((String) message.get("message"));
            content = (JSONObject) o;
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return content;
        }
    }
}
