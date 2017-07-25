package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;

/**
 * This class is used to create both the shell and control channels.
 *
 * Shell: this single ROUTER socket allows multiple incoming connections from frontends,
 * and this is the socket where requests for code execution, object information, prompts, etc.
 * are made to the kernel by any frontend. The communication on this socket is a sequence of request/reply actions
 * from each frontend and the kernel.
 *
 * Control: This channel is identical to Shell, but operates on a separate socket,
 * to allow important messages to avoid queueing behind execution requests (e.g. shutdown or abort).
 *
 * Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html
 * Shell documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-shell-router-dealer-channel
 *
 * EXTENDS JupyterChannel : the abstract class implementing the default behavior of a Jupyter channel.
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

    /**
     * Send a KernelInfoRequest message.
     *
     * Here are more information : http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info
     */
    public void sendKernelInfoRequest() {
        messagesManager.sendMessageOnShell().sendKernelInfoRequestMessage();
    }

}
