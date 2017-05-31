package fr.irisa.diverse.FBPNetworkProtocol;

/**
 * Created by antoine on 26/05/2017.
 */
public class TraceMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    private FBPNetworkProtocolManager owningManager;

    // Constructor
    public TraceMessageHandler (FBPNetworkProtocolManager manager) {
        this.PROTOCOL = "trace";
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