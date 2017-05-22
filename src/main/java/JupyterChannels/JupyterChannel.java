package JupyterChannels;

import Core.Kernel;
import JupyterMessaging.Manager;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


/**
 * Created by antoine on 28/04/17.
 */
public abstract class JupyterChannel implements Runnable {

    protected String name;
    protected Context context = null;
    protected Socket socket = null;
    protected String socketAddress;
    protected String identity;
    protected int socketType;
    protected boolean connected = false;
    protected boolean log = false;
    protected Kernel owningKernel;
    protected Manager messagesManager;
    protected Thread thread;

    public JupyterChannel(String name, String transport, String ip, long port, String containerID, int socketType, Kernel kernel) {
        // Store the name & type
        this.name = name;
        this.socketType = socketType;
        this.owningKernel = kernel;

        // Create the ZMQ context and the socket (without connecting it)
        this.context = ZMQ.context(1);
        this.socket = context.socket(socketType);
        // Set Linger to 1s to prevent hangs at exit
        this.socket.setLinger(1000);

        // Store the address of the socket and set its identity
        setIdentity(containerID);
        this.socketAddress = transport + "://" + ip + ":" + port;
        socket.setIdentity(containerID.getBytes());
    }

    /*==================================================================================================================
                                               THREAD RELATED METHODS
     =================================================================================================================*/

    /**
     * Run methods from Runnable interface
     */
    public void run() {
        initializeThread();

        // Loop that will run whenever the Thread runs
        // This is where we will handle the socket behavior
        while(!Thread.currentThread().isInterrupted()) {

            // We look for the delimiter to start handling the message
            String recv1 = "";
            String recv2 = socket.recvStr();

            while(!recv2.equals("<IDS|MSG>")) {

                // Log lost data
                System.out.println("[WARNING] Loosing data on socket " + name + " : " + recv1);

                // Move values to continue searching for the delimiter
                recv1 = recv2;
                recv2 = socket.recvStr();
            }

            String uuid = recv1;
            String delimiter = recv2;
            String hmac = socket.recvStr();
            String header = socket.recvStr();
            String parent_header = socket.recvStr();
            String metadata = socket.recvStr();
            String content = socket.recvStr();

            if(this.log) logMessage(uuid, delimiter, hmac, header, parent_header, metadata, content);

            handleMessage(uuid, delimiter, hmac, header, parent_header, metadata, content);
        } // End while

        stopThread();
    }

    /**
     * Start the thread that makes the channel working
     */
    public void start() {
        if(thread == null) thread = new Thread(this);
        this.messagesManager = owningKernel.getMessagesManager();
        thread.start();
    }

    /**
     * Interrupt the channel by interrupting the thread
     */
    public void stop() throws InterruptedException {
        if(thread != null && !thread.isInterrupted()) thread.interrupt();

        while(!thread.isInterrupted()) {
            Thread.sleep(100);
        }
    }

    /**
     * Resume the channel and reconnect it
     */
    public void resume() {
        if(thread != null) thread.run();
    }

    /*==================================================================================================================
                                               CUSTOM METHODS
     =================================================================================================================*/

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
    private void handleMessage(String uuid, String delimiter, String hmac, String header, String parent_header, String metadata, String content) {
        String[] incomingMessage = {uuid, delimiter, hmac, header, parent_header, metadata, content};

        messagesManager.handleMessage(name, incomingMessage);
    }

    /**
     * If true, the channel will log every message it receives. Otherwise, doesn't log anything.
     * @param log : boolean
     */
    public void doLog (boolean log) {
        this.log = log;
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
        System.out.println("\n------- MESSAGE RECEIVED ON " + name + " CHANNEL -------");
        System.out.println("UUID : " + uuid);
        System.out.println("Delimiter : " + delimiter);
        System.out.println("Hmac : " + hmac);
        System.out.println("Header : " + header);
        System.out.println("Parent_header : " + parent_header);
        System.out.println("Metadata : " + metadata);
        System.out.println("Content : " + content);
        System.out.println("\n");
    }


    public boolean isRunning() {
        return thread.isAlive();
    }

    public void setIdentity(String identity) {
        this.identity = identity;
        this.socket.setIdentity(identity.getBytes());
    }

    /*==================================================================================================================
                                        METHODS IMPLEMENTED BY LOWER CLASSES
     =================================================================================================================*/

    protected abstract void initializeThread();

    protected abstract void stopThread();
}
