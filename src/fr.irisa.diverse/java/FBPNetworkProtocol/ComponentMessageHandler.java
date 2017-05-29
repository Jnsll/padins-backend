package FBPNetworkProtocol;

import Flow.Component;
import Flow.ComponentsUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by antoine on 26/05/2017.
 */
public class ComponentMessageHandler implements FBPProtocolHandler {

    // Attributes
    FBPNetworkProtocolManager owningManager;
    String PROTOCOL = "component";

    // Constructor
    public ComponentMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "list" :
                list(message);
                break;
            case "getsource" :
                getsource(message);
                break;
            default :
                System.err.println("Received message for unknown protocol : " + message.toJSONString());
                sendError("Error with message : " + message.toJSONString());
                break;
        }

    }

    /* =================================================================================================================
                                                      PRIVATE METHODS
       ===============================================================================================================*/

    /** Request a list of currently available components. Will be responded with a set of `component` messages.
     *
     * @param message : received message
     */
    private void list(FBPMessage message) {
        ArrayList<Component> components = ComponentsUtils.getComponentsFromLib();

        // Send a component message for each component
        for (int i=0; i<components.size(); i++) {
            sendComponentMessage(components.get(i));
        }

        // Finally : send a components ready message
        sendComponentReadyMessage();
    }

    /** Request for the source code of a given component. Will be responded with a `source` message.
     *
     * @param message : received message
     */
    private void getsource(FBPMessage message) {
        String name = (String) message.getPayloadAsJSON().get("name");
        int slashIndex = name.lastIndexOf('/');
        String component = name.substring(slashIndex, name.length());

        // TODO : finish with link https://flowbased.github.io/fbp-protocol/#component-getsource

    }

    /* =================================================================================================================
                                              PRIVATE METHODS TO SEND MESSAGES
       ===============================================================================================================*/

    private void sendComponentMessage (Component component) {
        // Retrieve the component as a serialized JSON
        String payload = component.toString();

        // Create the FBP Message
        FBPMessage msg = new FBPMessage(PROTOCOL, "component", payload);

        // Send it
        owningManager.send(msg.toJSONString());
    }

    private void sendComponentReadyMessage () {
        FBPMessage msg = new FBPMessage(PROTOCOL, "componentsready", "");

        owningManager.send(msg.toJSONString());
    }

    private void sendError(String error) {
        JSONObject obj = new JSONObject();
        obj.put("message", error);
        String payload = obj.toJSONString();

        FBPMessage msg = new FBPMessage(PROTOCOL, "error", payload);
    }

}
