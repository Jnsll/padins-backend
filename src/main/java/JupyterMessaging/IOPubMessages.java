package JupyterMessaging;

import Core.Kernel;

/**
 * Created by antoine on 10/05/2017.
 */
public class IOPubMessages {

    private Kernel kernel = null;

    public IOPubMessages (Kernel kernel) {
        this.kernel = kernel;
    }

    public void handleMessage (String type, JupyterMessage message) {
        // TODO
    }

    /* =================================================================================================================
                                         METHODS TO HANDLE INCOMING IOPUB MESSAGES
     =================================================================================================================*/

    private void handleStatusMessage (JupyterMessage message) {
        // TODO
    }

}
