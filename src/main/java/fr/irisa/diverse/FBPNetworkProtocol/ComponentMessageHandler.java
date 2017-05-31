package fr.irisa.diverse.FBPNetworkProtocol;

import fr.irisa.diverse.Flow.Component;
import fr.irisa.diverse.Flow.ComponentsUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by antoine on 26/05/2017.
 */
public class ComponentMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    private String componentsLibrary = "";

    // Constructor
    public ComponentMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
        this.componentsLibrary = owningManager.getComponentsLibrary();
        this.PROTOCOL = "component";
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "list" :
                list();
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
     */
    private void list() {
        ArrayList<Component> components = ComponentsUtils.getComponentsFromLib(componentsLibrary);

        // Send a component message for each component
        for (Component component : components) {
            sendComponentMessage(component);
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
        // Send it directly as json
        sendMessage("component", component.toJson());
    }

    private void sendComponentReadyMessage () {
        sendMessage("componentsready", new JSONObject());
    }

    private void sendSourceMessage (String library, String component) {
        Component component1 = ComponentsUtils.getComponent(library, component);

        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("name", component);
        payload.put("language", component1 != null ? component1.getLanguage() : "");
        payload.put("library", library);
        payload.put("code", component1.getCode());
        payload.put("tests", component1.getTests());

        // Send it
        sendMessage("source", payload);
    }

}
