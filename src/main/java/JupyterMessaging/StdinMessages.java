package JupyterMessaging;

import Core.Kernel;

/**
 * Created by antoine on 10/05/2017.
 */
public class StdinMessages {

    // Attributes
    private Kernel kernel = null;

    // Constructor
    public StdinMessages (Kernel kernel) {
        this.kernel = kernel;
    }

    public void handleMessage (String type, JupyterMessage message) {
        switch (type) {
            // TODO
            default :
                System.err.println("Received unknown message on stdin channel : " + message.getMessageToSend());
        }
    }

    /* =================================================================================================================
                                       METHODS TO REACT TO INCOMING STDIN MESSAGES
     =================================================================================================================*/

    // TODO
}
