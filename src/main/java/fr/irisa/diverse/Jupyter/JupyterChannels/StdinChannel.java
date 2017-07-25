package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;

/**
 * stdin: this ROUTER socket is connected to all frontends, and it allows the kernel to request input from the active
 * frontend when raw_input() is called. The frontend that executed the code has a DEALER socket that acts
 * as a ‘virtual keyboard’ for the kernel while this communication is happening
 * (illustrated in the figure by the black outline around the central keyboard).
 * In practice, frontends may display such kernel requests using a special input widget or otherwise indicating
 * that the user is to type input for the kernel instead of normal commands in the frontend.
 *
 * Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html
 * Stdin documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-stdin-router-dealer-channel
 *
 * Created by antoine on 03/05/17.
 */
public class StdinChannel extends JupyterChannel {

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

    public StdinChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER, kernel);
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
        this.connected = true;
    }

    /**
     * Send a message as bytes, needed by Jupyter
     * @param message : the message to send to the shell
     */
    public void send (String[] message) {
        for(int i=0; i<message.length-1; i++) {
            socket.sendMore(message[i].getBytes());
        }
        socket.send(message[message.length-1]);
    }
}
