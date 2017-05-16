package JupyterMessaging;

import Core.Kernel;
import org.json.simple.JSONObject;

/** The manager is a component that handle everything related to incoming messages
 *
 * Created by antoine on 16/05/2017.
 */
public class Manager {

    // Attributes
    private Kernel owningKernel = null;
    private ShellMessages shellMessages = null;
    private IOPubMessages ioPubMessages = null;
    private StdinMessages stdinMessages = null;

    public Manager (Kernel kernel) {
        owningKernel = kernel;
        shellMessages = new ShellMessages(owningKernel);
        ioPubMessages = new IOPubMessages(owningKernel);
        stdinMessages = new StdinMessages(owningKernel);
    }

    public void handleMessage (String sourceChannel, String[] incomingMessage) {
        JupyterMessage message = new JupyterMessage(owningKernel, incomingMessage);

        if(hmacIsCorrect(message)) {
            handleHeader(message.getHeader());

            String type = (String) message.getHeader().get("msg_type");

            switch (sourceChannel) {
                case "shell" :
                    shellMessages.handleMessage(type, message);
                    break;
                case "iopub" :
                    ioPubMessages.handleMessage(type, message);
                    break;
                case "stdin" :
                    stdinMessages.handleMessage(type, message);
                    break;
                case "control" :
                    shellMessages.handleMessage(type, message);
                    break;
                default :
                    System.err.println("Manager.java : error with the sourceChannel name");
                    break;
            }
        } else {
            System.err.println("Incorrect hmac in message : " + message.getMessageToSend());
        }
    }

    /* =================================================================================================================
                                           MESSAGE HEADER RELATED METHODS
     =================================================================================================================*/

    private boolean hmacIsCorrect(JupyterMessage message) {
        // TODO
        return true;
    }

    private void handleHeader(JSONObject header) {
        // TODO
    }

}
