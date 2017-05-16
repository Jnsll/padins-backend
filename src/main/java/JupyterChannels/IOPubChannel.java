package JupyterChannels;

import Core.Kernel;
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
     */

    public IOPubChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.SUB, kernel);
    }

    /**
     * Run methods from Runnable interface
     */
    public void run() {
        // First : connect the server
        this.socket.connect(this.socketAddress);
        this.socket.subscribe("".getBytes());
        this.connected = true;
        System.out.println("Connected to IOPub publisher on : " + this.socketAddress);

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

            handleMessage(uuid, delimiter, hmac, header, parent_header, metadata, content);

            if(this.log) logMessage(uuid, delimiter, hmac, header, parent_header, metadata, content);
        }

        // When stopping the thread : terminate the context & close socket
        this.socket.close();
        this.context.term();
        this.connected = false;
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM METHODS
       =================================================================================================================
       ===============================================================================================================*/

    /** React depending on the received message
     *
     * @param uuid : see "Messaging in Jupyter" doc
     * @param delimiter : see "Messaging in Jupyter" doc
     * @param hmac : see "Messaging in Jupyter" doc
     * @param header : see "Messaging in Jupyter" doc
     * @param parent_header : see "Messaging in Jupyter" doc
     * @param metadata : see "Messaging in Jupyter" doc
     * @param content : see "Messaging in Jupyter" doc
     */
        private void handleMessage(String uuid, String delimiter, String hmac, String header, String parent_header,
                                   String metadata, String content) {
            // TODO : implement this method properly
            // To do that look at : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-iopub-pub-sub-channel

            if (owningKernel.getIdentity() == "") setKernelsIdentity(uuid);
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
            System.out.println("\n------- MESSAGE RECEIVED ON IOPUB CHANNEL -------");
            System.out.println("UUID : " + uuid);
            System.out.println("Delimiter : " + delimiter);
            System.out.println("Hmac : " + hmac);
            System.out.println("Header : " + header);
            System.out.println("Parent_header : " + parent_header);
            System.out.println("Metadata : " + metadata);
            System.out.println("Content : " + content);
            System.out.println("\n");
        }

    /**
     * Set the ZMQ identity, used in messages for the kernel on this server-side. The kernel identity (from docker)
     * is formatted as : kernel.{u-u-i-d}.{message}
     * We retrieve the u-u-i-d and store it as our kernel's identity
     * @param kernelId : kernel's uuid retrieve from the first message coming from the jupyter kernel
     */
        private void setKernelsIdentity (String kernelId) {
            // UUID is formatted like this : kernel.b1a0e4c3-bb70-49c3-b1f1-b6d79b5f0edf.status
            // and we want only the part between the two dots
            String identity = kernelId;
            int indexOfFirstDot = identity.indexOf('.') + 1;
            int indexOfSecondDot = identity.indexOf('.', indexOfFirstDot );
            identity = identity.substring(indexOfFirstDot, indexOfSecondDot);

            owningKernel.setIdentity(identity);
        }

}
