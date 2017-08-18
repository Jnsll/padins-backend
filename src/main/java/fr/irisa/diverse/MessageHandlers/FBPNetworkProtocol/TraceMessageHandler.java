package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

/**
 * Class managing the Trace Message for the Flow-Based Programming Network Protocol
 * To know more about this protocol, take a look at the doc on J.Paul Morisson's website :
 * https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
public class TraceMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    private FBPNetworkProtocolManager owningManager;

    /* =================================================================================================================
                                                    CONSTRUCTOR
       ===============================================================================================================*/

    TraceMessageHandler (FBPNetworkProtocolManager manager) {
        this.PROTOCOL = "trace";
        this.owningManager = manager;
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    /**
     * Handle a message.
     * It call the corresponding method for each supported type of message.
     *
     * @param message : the message to handle
     */
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