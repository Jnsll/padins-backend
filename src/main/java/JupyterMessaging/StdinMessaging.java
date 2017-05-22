package JupyterMessaging;

import Core.Kernel;
import JupyterChannels.StdinChannel;
import org.json.simple.JSONObject;

/** Implementation of the messaging mechanism for the Stdin channel. For more information :
 * http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel
 *
 * Created by antoine on 10/05/2017.
 */
public class StdinMessaging {

    // Attributes
    private Kernel kernel = null;
    private StdinChannel channel = null;

    // Constructor
    public StdinMessaging(Kernel kernel, StdinChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
    }

    public void handleMessage (String type, JupyterMessage message) {
        switch (type) {
            case "input_request" :
                handleInputRequestMessage(message);
            default :
                System.err.println("Received unknown message on stdin channel : " + message.getMessageToSend());
        }
    }

    /* =================================================================================================================
                                       METHODS TO REACT TO INCOMING STDIN MESSAGES
     =================================================================================================================*/

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
        channel.send(reply.toString());
    }
}
