package FBPNetworkProtocol;

import org.json.simple.JSONObject;

/**
 * Created by antoine on 26/05/2017.
 */
public class ComponentMessageHandler implements FBPProtocolHandler {

    // Attributes

    // Constructor
    public ComponentMessageHandler () {

    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (JSONObject message) {
        String messageType = (String) message.get("message_type");
        JSONObject content = FBPProtocolHandler.getContent(message);

        switch (messageType) {
            // TODO
        }

    }

    /* =================================================================================================================
                                                      PRIVATE METHODS
       ===============================================================================================================*/

}
