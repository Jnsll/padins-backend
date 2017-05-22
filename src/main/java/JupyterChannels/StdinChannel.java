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
    private ZMQ.Poller items;

    public StdinChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER, kernel);
    }

    /**
     * Run methods from Runnable interface
     */
    public void run() {
        // First : connect the server
        this.socket.connect(this.socketAddress);
        this.connected = true;

        // Initialize poller to read message when they arrive
        items = this.context.poller(1);
        items.register(socket, ZMQ.Poller.POLLIN);

        // Loop that will run whenever the Thread runs
        // This is where we will handle the socket behavior
        while(!Thread.currentThread().isInterrupted()) {

            // Then check whether a message has been received or not
            byte[] message;
            items.poll();
            if(items.pollin(0)) {
                message = socket.recv(0);
                // TODO : handle the message
                if(this.log) {
                    System.out.println("Received : " + new String(message) + " on socket " + name);
                }
            }
        } // End while

        // When stopping the thread : destroy the context & not connected anymore
        this.socket.disconnect(socketAddress);
        this.context.term();
        this.connected = false;
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM METHODS
       =================================================================================================================
       ===============================================================================================================*/

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
