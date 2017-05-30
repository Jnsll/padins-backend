package FBPNetworkProtocol;

import org.json.simple.JSONObject;

/**
 * Created by antoine on 26/05/2017.
 */
public class NetworkMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    FBPNetworkProtocolManager owningManager;

    // Constructor
    public NetworkMessageHandler (FBPNetworkProtocolManager manager) {
        this.PROTOCOL = "network";
        this.owningManager = manager;
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "persist" :
                persist(message);
                break;
            case "getstatus" :
                getstatus(message);
                break;
            case "edges" :
                edges(message);
                break;
            case "start" :
                start(message);
                break;
            case "stop" :
                stop(message);
                break;
            default:
                System.err.println("[ERROR] Unknown message on Network : " + message.toJSONString());
                break;
        }

    }

    /* =================================================================================================================
                                         HANDLERS FOR RECEIVED MESSAGE METHODS
       ===============================================================================================================*/

    private void persist(FBPMessage message) {
        // TODO : owningManager.respondToAllWith(createPersist());
    }

    private void getstatus(FBPMessage message) {
    }

    private void edges(FBPMessage message) {
    }

    private void start(FBPMessage message) {
    }

    private void stop(FBPMessage message) {
    }

    /* =================================================================================================================
                                            METHODS TO CREATE RESPONSES
       ===============================================================================================================*/

    private FBPMessage createPersist (FBPMessage message) { return message; }

}