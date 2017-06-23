package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

/**
 * A FBPProtocolHandler is a component that handle message on a specific FBP Network Protocol sub-protocol.
 * To know more about FBPNP sub-protocols : https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
interface FBPProtocolHandler {

    /** Handle a received message
     *
     * @param message : the message to handle
     */
    void handleMessage(FBPMessage message);

}
