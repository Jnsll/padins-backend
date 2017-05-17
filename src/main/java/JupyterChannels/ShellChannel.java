package JupyterChannels;

import Core.Kernel;
import org.zeromq.ZMQ;

import java.util.ArrayList;

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
    private ZMQ.Poller items;
    private ArrayList<String[]> queue;

    public ShellChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER, kernel);

        // Instantiate the queue containing the message to send
        queue = new ArrayList<>();
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

            // First : check whether there is a message to send
            if(queue.size() > 0 && owningKernel.isIdle()) {
                // Send the first message and remove it from the queue
                // TODO : test
                sendQueuedMessage(queue.get(0));
                queue.remove(0);
            }

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
                                                    CUSTOM CHANNEL METHODS
       =================================================================================================================
       ===============================================================================================================*/

    /**
     * Add a message in the queue of to be send messages
     * @param message : the message to send to the shell
     */
    public void send (String[] message) {
        queue.add(message);
    }

    /**
     * Method used only in this class !
     * Send a message through the socket
     * @param message
     */
    private void sendQueuedMessage (String[] message) {
        System.out.println("[INFO] Sending message !");
        for(int i=0; i<message.length; i++){
            socket.send(message[i]);
        }
    }
}
