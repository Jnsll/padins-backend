package JupyterMessaging;

import Core.Kernel;
import JupyterChannels.IOPubChannel;

/**
 * Created by antoine on 10/05/2017.
 */
public class IOPubMessaging {

    // Attributes
    private Kernel kernel = null;
    private IOPubChannel channel = null;

    // Constructor
    public IOPubMessaging(Kernel kernel, IOPubChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
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
