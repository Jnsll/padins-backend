package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;

/**
 * Created by antoine on 03/05/17.
 */
public class IOPubChannel extends JupyterChannel {

    /* Superclass attributes
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
        System.out.println("Connected to IOPub publisher on : " + this.socketAddress);
    }

}
