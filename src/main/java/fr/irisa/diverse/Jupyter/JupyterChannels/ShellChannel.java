package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;

/**
 * This class is used to create both the shell and control channels
 *
 * Created by antoine on 03/05/17.
 */
public class ShellChannel extends JupyterChannel {

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

    public ShellChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER, kernel);
    }

    @Override
    protected void initializeThread() {
        // First : connect the server
        this.socket.connect(this.socketAddress);
        this.connected = true;

        // Send kernel_info request
        if (name.equals("shell")) messagesManager.sendMessageOnShell().sendKernelInfoRequestMessage();
    }


    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM CHANNEL METHODS
       =================================================================================================================
       ===============================================================================================================*/

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
