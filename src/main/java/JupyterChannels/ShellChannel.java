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

    public ShellChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER, kernel);
    }

    /**
     * Run methods from Runnable interface
     */
    public void run() {
        // First : connect the server
        this.socket.connect(this.socketAddress);
        this.connected = true;

        // Send kernel_info request
        //if (name.equals("shell")) messagesManager.sendMessageOnShell().sendKernelInfoRequestMessage();

        // Loop that will run whenever the Thread runs
        // This is where we will handle the socket behavior
        while(!Thread.currentThread().isInterrupted()) {

            String uuid = socket.recvStr();
            String delimiter = socket.recvStr();
            String hmac = socket.recvStr();
            String header = socket.recvStr();
            String parent_header = socket.recvStr();
            String metadata = socket.recvStr();
            String content = socket.recvStr();

            if(this.log) logMessage(uuid, delimiter, hmac, header, parent_header, metadata, content);

            //handleMessage(uuid, delimiter, hmac, header, parent_header, metadata, content);
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
     * Send a message as bytes, needed by Jupyter
     * @param message : the message to send to the shell
     */
    public void send (String[] message) {
        for(int i=0; i<message.length-1; i++) {
            socket.sendMore(message[i].getBytes());
        }
        socket.send(message[message.length-1]);
    }

    /** Log all the messages received with their category name
     *
     * @param uuid : see "Messaging in Jupyter" doc
     * @param delimiter : see "Messaging in Jupyter" doc
     * @param hmac : see "Messaging in Jupyter" doc
     * @param header : see "Messaging in Jupyter" doc
     * @param parent_header : see "Messaging in Jupyter" doc
     * @param metadata : see "Messaging in Jupyter" doc
     * @param content : see "Messaging in Jupyter" doc
     */
    private void logMessage (String uuid, String delimiter, String hmac, String header, String parent_header,
                             String metadata, String content) {
        System.out.println("\n------- MESSAGE RECEIVED ON SHELL CHANNEL -------");
        System.out.println("UUID : " + uuid);
        System.out.println("Delimiter : " + delimiter);
        System.out.println("Hmac : " + hmac);
        System.out.println("Header : " + header);
        System.out.println("Parent_header : " + parent_header);
        System.out.println("Metadata : " + metadata);
        System.out.println("Content : " + content);
        System.out.println("\n");
    }

}
