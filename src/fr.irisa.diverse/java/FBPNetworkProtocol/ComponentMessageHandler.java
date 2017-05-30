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
    private FBPNetworkProtocolManager owningManager;
    private String componentsLibrary = "";
    final String PROTOCOL = "component";

    // Constructor
    public ComponentMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
        this.componentsLibrary = owningManager.getComponentsLibrary();
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
                owningManager.sendError(PROTOCOL, "Error with message : " + message.toJSONString());
                break;
        }

    }

    /* =================================================================================================================
                                    PRIVATE METHODS TO HANDLE RECEIVED MESSAGES
       ===============================================================================================================*/

    /** Request a list of currently available components. Will be responded with a set of `component` messages.
     *
     * @param message : received message
     */
    private void list(FBPMessage message) {
        ArrayList<Component> components = ComponentsUtils.getComponentsFromLib(componentsLibrary);

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
        String library = name.substring(0, slashIndex);
        String component = name.substring(slashIndex + 1, name.length());

        sendSourceMessage(library, component);

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

    private void sendSourceMessage (String library, String component) {
        Component component1 = ComponentsUtils.getComponent(library, component);

        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("name", component);
        payload.put("language", component1.getLanguage());
        payload.put("library", library);
        payload.put("code", component1.getCode());
        payload.put("tests", component1.getTests());

        // Build the message that will be sent
        FBPMessage msg = new FBPMessage(PROTOCOL, "source", payload.toJSONString());

        owningManager.send(msg.toJSONString());
    }

}
