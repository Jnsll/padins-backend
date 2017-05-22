package JupyterChannels;

import Core.Kernel;
import org.zeromq.ZMQ;

import java.util.ArrayList;

/**
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

    @Override
    protected void stopThread() {
        // When stopping the thread : destroy the context & not connected anymore
        this.socket.disconnect(socketAddress);
        this.context.term();
        this.connected = false;
    }

    /**
     * Send a message through the socket
     * @param message
     */
    public void send (String message) {
        // TODO : make sure it works, as in ShellChannel
        System.out.println("[INFO] Sending message on STDIN !");

        socket.send(message.getBytes(), 0);
    }
}
