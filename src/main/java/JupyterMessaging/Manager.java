package JupyterMessaging;

import Core.Kernel;
import org.json.simple.JSONObject;

/** The manager is a component that handle everything related to reacting to incoming messages
 *
 * Created by antoine on 16/05/2017.
 */
public class Manager {

    // Attributes
    private Kernel owningKernel = null;
    private ShellMessaging shellMessaging = null;
    private IOPubMessaging ioPubMessaging = null;
    private StdinMessaging stdinMessaging = null;
    private ShellMessaging controlMessaging = null;

    public Manager (Kernel kernel) {
        owningKernel = kernel;
        shellMessaging = new ShellMessaging(owningKernel, kernel.shell);
        ioPubMessaging = new IOPubMessaging(owningKernel, kernel.iopub);
        stdinMessaging = new StdinMessaging(owningKernel, kernel.stdin);
        controlMessaging = new ShellMessaging(owningKernel, kernel.control);
    }

    public void handleMessage (String sourceChannel, String[] incomingMessage) {
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

    public ShellMessaging sendMessageOnShell () {
        return shellMessaging;
    }

    public StdinMessaging respondeOnStdin () {
        return stdinMessaging;
    }

    public ShellMessaging sendMessageOnControl () {
        return controlMessaging;
    }

    /* =================================================================================================================
                                           MESSAGE HEADER RELATED METHODS
     =================================================================================================================*/

    private boolean hmacIsCorrect(JupyterMessage message) {
        // TODO
        return true;
    }

    private void handleHeader(JSONObject header) {

        // Retrieve the session id and set it to the kernel
        String session = (String) header.get("session");
        owningKernel.setSession(session);
    }

    private void handleUUID (String uuid) {
        if (owningKernel.getIdentity().equals("")) setKernelsIdentity(uuid);
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
