package FBPNetworkProtocol;

/**
 * Created by antoine on 26/05/2017.
 */
public class ComponentMessageHandler implements FBPProtocolHandler {

    // Attributes
    FBPNetworkProtocolManager owningManager;

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
            // TODO
        }

    }

    /* =================================================================================================================
                                                      PRIVATE METHODS
       ===============================================================================================================*/

}
