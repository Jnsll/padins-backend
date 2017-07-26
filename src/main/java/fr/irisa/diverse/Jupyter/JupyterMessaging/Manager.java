package fr.irisa.diverse.Jupyter.JupyterMessaging;

import fr.irisa.diverse.Core.Kernel;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/** The manager is a component that handle everything related to reacting to incoming messages.
 *
 * It is the main class to use in order to handle the messages coming from a Jupyter Kernel.
 *
 * It has a main method that is handleMessage. This method takes the sourceChannel and the incoming message
 * and redirect it to the proper handler.
 *
 * Created by antoine on 16/05/2017.
 */
public class Manager {

    /* =================================================================================================================
                                                ATTRIBUTES
     =================================================================================================================*/

    private Kernel owningKernel = null;
    private ShellMessaging shellMessaging = null;
    private IOPubMessaging ioPubMessaging = null;
    private StdinMessaging stdinMessaging = null;
    private ShellMessaging controlMessaging = null;

    /* =================================================================================================================
                                                CONSTRUCTOR
     =================================================================================================================*/

    public Manager (Kernel kernel) {
        owningKernel = kernel;
        shellMessaging = new ShellMessaging(owningKernel, kernel.shell);
        ioPubMessaging = new IOPubMessaging(owningKernel, kernel.iopub);
        stdinMessaging = new StdinMessaging(owningKernel, kernel.stdin);
        controlMessaging = new ShellMessaging(owningKernel, kernel.control);
    }

    /* =================================================================================================================
                                           PUBLIC METHODS
     =================================================================================================================*/

    /**
     * Handles any Jupyter Message coming from a channel.
     *
     * @param sourceChannel {String} the name of the channel the message comes from
     * @param incomingMessage {String[]} the received message
     */
    public void handleMessage (String sourceChannel, ArrayList<String> incomingMessage) {
        JupyterMessage message = new JupyterMessage(owningKernel, incomingMessage);

        if(hmacIsCorrect(message)) {
            handleUUID(message.getUuid());
            handleHeader(message.getHeader());

            String type = (String) message.getHeader().get("msg_type");

            switch (sourceChannel) {
                case "shell" :
                    shellMessaging.handleMessage(type, message);
                    break;
                case "iopub" :
                    ioPubMessaging.handleMessage(type, message);
                    break;
                case "stdin" :
                    stdinMessaging.handleMessage(type, message);
                    break;
                case "control" :
                    shellMessaging.handleMessage(type, message);
                    break;
                default :
                    System.err.println("Manager.java : error with the sourceChannel name");
                    break;
            }
        } else {
            System.err.println("Incorrect hmac in message : " + message.getMessageToSend());
        }
    }

    /**
     * Send a message on the shell channel, using this method's returned object followed by
     * a call to the method sending the message you want to send.
     *
     * @return {ShellMessaging} the component that handle sending correctly formatted messages over the shell channel.
     */
    public ShellMessaging sendMessageOnShell () {
        return shellMessaging;
    }

    /**
     * Respond to a prompt request on Stdin, using this method's returned object followed by a call to
     * the method to answer on the channel.
     *
     * @return {StdinMessaging} the component that handle sending correctly formatted messages over the stdin channel
     */
    public StdinMessaging respondeOnStdin () {
        return stdinMessaging;
    }

    /**
     * Send a message on the control channel, using this method's returned object followed by
     * a call to the method sending the message you want to send.
     *
     * @return {ShellMessaging} the component that handle sending correctly formatted messages over the control channel.
     */
    public ShellMessaging sendMessageOnControl () {
        return controlMessaging;
    }

    /* =================================================================================================================
                                           MESSAGE HEADER RELATED METHODS
     =================================================================================================================*/

    /**
     * Verify that the HMAC in the given message is correct, according to the Jupyter documentation.
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#the-wire-protocol
     *
     * @param message {JupyterMessage} the message to test
     * @return {boolean} True if correct, false if not
     */
    private boolean hmacIsCorrect(JupyterMessage message) {
        /* TODO : the below code isn't doing the job. We need to figure out what the problem is.
         It is surprising, this code is doing the same thing as session.sign in the jupyter_client project */

        /*String signature = message.getHmac();

        // To obtain the check we generate a new hmac in the message, benefiting from its generate hmac method.
        String check = message.generateHmac();

        System.out.println("Hmac verification returns : " + signature.equals(check) +"\n" + signature + "\n" + check);
        return signature.equals(check);*/
        return true;
    }

    /**
     * Handle the header from the received message.
     * The header contains : String msg_id, String username, String session, String date, String msg_type,
     * String version="5.0"
     *
     * @param header {JSONObject} the header to handle its content
     */
    private void handleHeader(JSONObject header) {

        // Retrieve the session id and set it to the kernel
        String session = (String) header.get("session");
        owningKernel.setSession(session);
    }

    /**
     * Handle the given uuid by using it as the identity of the kernel.
     *
     * @param uuid {String} the uuid to handle
     */
    private void handleUUID (String uuid) {
        if (owningKernel.getIdentity().equals("") && uuid != null && !uuid.equals("")) setKernelsIdentity(uuid);
    }

    /* =================================================================================================================
                                                CUSTOM METHODS
     =================================================================================================================*/

    /**
     * Set the ZMQ identity, used in messages for the kernel on this server-side. The kernel identity (from docker)
     * is formatted as : kernel.{u-u-i-d}.{message}
     * We retrieve the u-u-i-d and store it as our kernel's identity
     * @param kernelId : kernel's uuid retrieve from the first message coming from the jupyter kernel
     */
    private void setKernelsIdentity (String kernelId) {
        // UUID is formatted like this : kernel.b1a0e4c3-bb70-49c3-b1f1-b6d79b5f0edf.status
        // and we want only the part between the two dots
        String identity = kernelId;
        int indexOfFirstDot = identity.indexOf('.') + 1;
        int indexOfSecondDot = identity.indexOf('.', indexOfFirstDot );
        identity = identity.substring(indexOfFirstDot, indexOfSecondDot);

        owningKernel.setIdentity(identity);
    }

}
