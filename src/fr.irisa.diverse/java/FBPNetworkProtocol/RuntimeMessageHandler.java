package FBPNetworkProtocol;

/**
 * Created by antoine on 26/05/2017.
 */
public class RuntimeMessageHandler implements FBPProtocolHandler {

    // Attributes
    FBPNetworkProtocolManager owningManager;

    // Constructor
    public RuntimeMessageHandler (FBPNetworkProtocolManager manager) {
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
