package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;

/**
 * IOPub: this socket is the ‘broadcast channel’ where the kernel publishes all side effects (stdout, stderr, etc.)
 * as well as the requests coming from any client over the shell socket and its own requests on the stdin socket.
 * There are a number of actions in Python which generate side effects: print() writes to sys.stdout,
 * errors generate tracebacks, etc. Additionally, in a multi-client scenario,
 * we want all frontends to be able to know what each other has sent to the kernel
 * (this can be useful in collaborative scenarios, for example).
 * This socket allows both side effects and the information about communications taking place
 * with one client over the shell channel to be made available to all clients in a uniform manner.
 *
 * Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html
 * IOPub channel documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-iopub-pub-sub-channel
 *
 * EXTENDS JupyterChannel : the abstract class implementing the default behavior of a Jupyter channel.
 *
 * Created by antoine on 03/05/17.
 */
public class IOPubChannel extends JupyterChannel {

    /* Superclass attributes
    -----------------------------------------
        String name;
        ZContext context = null;
        Socket socket = null;
        String socketAddress;
        String identity;
        boolean connected = false;
        boolean log = false;
        Kernel owningKernel;
        Manager messagesManager;
        Thread thread;
     */

    public IOPubChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.SUB, kernel);
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM METHODS
       =================================================================================================================
       ===============================================================================================================*/

    @Override
    protected void initializeThread() {
        // First : connect the server
        this.socket.connect(this.socketAddress);
        this.socket.subscribe("".getBytes());
        this.connected = true;
    }

}
