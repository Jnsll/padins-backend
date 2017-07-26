package fr.irisa.diverse.Jupyter.JupyterMessaging;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.Jupyter.JupyterChannels.StdinChannel;
import fr.irisa.diverse.Utils.Utils;
import org.json.simple.JSONObject;

/** Implementation of the messaging mechanism for the Stdin channel. For more information :
 * http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel
 *
 * Created by antoine on 10/05/2017.
 */
public class StdinMessaging {

    /* =================================================================================================================
                                               ATTRIBUTES
     =================================================================================================================*/

    private Kernel kernel = null;
    private StdinChannel channel = null;

    /* =================================================================================================================
                                                CONSTRUCTOR
     =================================================================================================================*/

    StdinMessaging(Kernel kernel, StdinChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
    }

    /**
     * Handle the given message, coming from the Stdin Channel
     * @param type {String} the type of the message
     * @param message {JupyterMessage} the message itself
     */
    public void handleMessage (String type, JupyterMessage message) {
        switch (type) {
            case "input_request" :
                handleInputRequestMessage(message);
            default :
                System.err.println("Received unknown message on stdin channel : " + Utils.StringArrayToString(message.getMessageToSend()));
        }
    }

    /* =================================================================================================================
                                       METHODS TO REACT TO INCOMING STDIN MESSAGES
     =================================================================================================================*/

    /**
     * Handle an input_request message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel
     *
     * Our implementation behavior: depends on the status of the message.
     * 1. Prompts all the UI for the answer
     * 2. Retrieve the answer and build the message
     * 3. Respond to the kernel with a input_reply message
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleInputRequestMessage (JupyterMessage message) {

        String prompt = (String) message.getContent().get("prompt"); // Prompt to display to user
        boolean password = (boolean) message.getContent().get("password");

        // TODO : prompt the UI for a real answer
        String userResponse = "";

        // Build the content dict
        JSONObject content = new JSONObject();
        content.put("value", userResponse);

        // Create correctly formatted message
        JupyterMessage reply = new JupyterMessage(kernel, "input_reply", null, null, content);

        // Send it through the socket
        channel.send(reply.getMessageToSend());
    }
}
