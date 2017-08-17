package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

import fr.irisa.diverse.Flow.Component;
import fr.irisa.diverse.Flow.ComponentsUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Class managing the Component messages for the Flow-Based Programming Network Protocol
 * To know more about this protocol, take a look at the doc on J.Paul Morisson's website :
 * https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
public class ComponentMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    private String componentsLibrary = "";

    /* =================================================================================================================
                                                        CONSTRUCTOR
       ===============================================================================================================*/
    ComponentMessageHandler (FBPNetworkProtocolManager manager) {
        this.owningManager = manager;
        this.componentsLibrary = owningManager.getComponentsLibrary();
        this.PROTOCOL = "component";
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    /**
     * Handle a new incoming message on the Component protocol.
     * It will redirect the message to the proper method
     *
     * @param message : the message to handle
     */
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
                sendError("Error with message : " + message.toJSONString());
                break;
        }

    }

    /* =================================================================================================================
                                    PRIVATE METHODS TO HANDLE RECEIVED MESSAGES
       ===============================================================================================================*/

    /**
     * Request the list of the currently available components.
     * Will respond with one 'component' message per available component
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

    /**
     * Request for the source code of a given component. Will be responded with a `source` message.
     *
     * @param message : received message
     */
    private void getsource(FBPMessage message) {
        // Retrieve the name of the component for which the user request the source code
        String name = (String) message.getPayload().get("name");
        int slashIndex = name.lastIndexOf('/');

        // Split the name of the library and the name of the component
        String library = name.substring(0, slashIndex);
        String component = name.substring(slashIndex + 1, name.length());

        // Send a source message for the component in the library
        sendSourceMessage(library, component);

    }

    /* =================================================================================================================
                                              PRIVATE METHODS TO SEND MESSAGES
       ===============================================================================================================*/

    /**
     * Send a component message to the client that made a request
     *
     * @param component : the component object to send
     */
    private void sendComponentMessage (Component component) {
        // Send it directly as json
        sendMessage("component", component.toJson());
    }

    /**
     * Send a componentsready message to the client that requested a list of component.
     * This message is used to prevent the UI that all the other components have been sent.
     */
    private void sendComponentReadyMessage () {
        sendMessage("componentsready", new JSONObject());
    }

    /**
     * Send a source message as described in the doc here :
     * https://flowbased.github.io/fbp-protocol/#component-source
     *
     * @param library : the library of components
     * @param component : the component for which to send the source code
     */
    @SuppressWarnings("unchecked")
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
