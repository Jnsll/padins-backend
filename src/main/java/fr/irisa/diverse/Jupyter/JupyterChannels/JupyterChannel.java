package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.Jupyter.JupyterMessaging.Manager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A Kernel uses 5 channels to communicates on both side (client/server). All this is explained here :
 * http://jupyter-client.readthedocs.io/en/latest/messaging.html#
 *
 * The Jupyter channel is the abstract class that implements the common behavior of all the Jupyter Channels of this
 * program.
 *
 * When needed, the child classes (Heartbeat, IOPub, Shell, Control, Stdin) override the necessary methods and attributes.
 *
 * Created by antoine on 28/04/17.
 */
public abstract class JupyterChannel implements Runnable {

    /*==================================================================================================================
                                                    ATTRIBUTES
     =================================================================================================================*/

    // Main attributes
    String name;
    private final int JUPYTER_MESSAGE_LENGTH = 7;
    private String lastCorrectUuidReceived = "";
    private ArrayList<String> incomingMessage = null;

    // ZMQ and socket related attributes
    Context context = null;
    Socket socket = null;
    String socketAddress;
    private String identity;
    private int socketType;

    // Communication state attributes
    boolean connected = false;
    boolean log = false;

    // Attributes related to this program architecture : the linked objects.
    Kernel owningKernel;
    Manager messagesManager;
    private Thread thread;

    //Attributes related to history
    private boolean storeHistory = true;
    private ArrayList<ArrayList<String>> history = null;

    /*==================================================================================================================
                                                    CONSTRUCTOR
     =================================================================================================================*/

    JupyterChannel(String name, String transport, String ip, long port, String containerID, int socketType, Kernel kernel) {
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

        // Create the incoming message & history object
        incomingMessage = new ArrayList<>();
        history = new ArrayList<>();
    }

    // Constructor that also set log and storeHistory properties
    public JupyterChannel(String name, String transport, String ip, long port, String containerID, int socketType, Kernel kernel, boolean shouldLog, boolean storeHistory) {
        this(name, transport, ip, port, containerID, socketType, kernel);

        this.log = shouldLog;
        this.storeHistory = storeHistory;

        if(storeHistory) history = new ArrayList<>();
    }

    /*==================================================================================================================
                                               THREAD RELATED METHODS
     =================================================================================================================*/

    /**
     * Run method from Runnable interface
     */
    public void run() {
        initializeThread();

        // Loop that will run whenever the Thread runs
        // This is where we will handle the socket behavior
        while(!Thread.currentThread().isInterrupted()) {

            try {
                // First : empty the incomingMessage because we are receiving a new one.
                incomingMessage = new ArrayList<>();

                // We look for the delimiter to start handling the message
                String lastMessageReceived = socket.recvStr();
                while (!lastMessageReceived.equals("<IDS|MSG>")) {
                    // We store the previous messages received, because we need the message right before the delimiter
                    // in order to make verifications.
                    // We look for the delimiter because it is always the same, so it makes its detection easier than
                    // the message before it.
                    incomingMessage.add(lastMessageReceived);

                    lastMessageReceived = socket.recvStr();
                }

                // Here we've received the delimiter.
                // Now we check whether the previously received message was a uuid or not
                if (incomingMessage.size() == 0) {
                    // It means that we did not received any uuid.
                    // So, we retrieve the previous one
                    incomingMessage.add(0, lastCorrectUuidReceived);
                } else if (!isUuid(incomingMessage.get(incomingMessage.size() - 1))) {
                    // Last received message is not a correct uuid, we remove everything from incoming message, log it and
                    for (int i = 0; i < incomingMessage.size(); i++) {
                        System.out.println("\033[33m" + "[WARNING]" + "\033[0m" + " Loosing data on " + name + " socket : " + incomingMessage.get(0));
                        incomingMessage.remove(0);
                    }
                    // add the correct uuid in the beginning
                    incomingMessage.add(0, lastCorrectUuidReceived);

                } else {
                    // Uuid is correct and incomingMessage.size > 0
                    if (incomingMessage.size() > 1) {
                        for (int i = 0; i < incomingMessage.size() - 1; i++) {
                            System.out.println("\033[33m" + "[WARNING]" + "\033[0m" + "Loosing data on " + name + " socket : " + incomingMessage.get(0));
                            incomingMessage.remove(0);
                        }
                    }

                    // Store the correct uuuid
                    lastCorrectUuidReceived = incomingMessage.get(incomingMessage.size() - 1);
                }

                incomingMessage.add(lastMessageReceived);   // delimiter <IDS|MSG>
                incomingMessage.add(socket.recvStr());      // hmac
                incomingMessage.add(socket.recvStr());      // header
                incomingMessage.add(socket.recvStr());      // parent_header
                incomingMessage.add(socket.recvStr());      // metadata
                incomingMessage.add(socket.recvStr());      // content

                // Log if configured
                if (this.log) logMessage(incomingMessage);
                // Save history if configured
                if (this.storeHistory) history.add(incomingMessage);

                // Finally handle the incoming message
                handleMessage(incomingMessage);
            } catch (ZMQException e) {
                // Catch a ZMQException in order to close the channel properly.
                // It is thrown only when we call context.term
                if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                    break;
                }
            }
        } // End while

        // End the thread by closing the socket properly
        socket.setLinger(0);
        socket.close();
        this.connected = false;
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
        if(thread != null && !thread.isInterrupted()) {
            context.term();
            thread.interrupt();
            thread.join();
        }
    }

    /*==================================================================================================================
                                             CUSTOM METHODS FOR USERS
     =================================================================================================================*/

    /**
     * If true, the channel will log every message it receives. Otherwise, doesn't log anything.
     * @param log : boolean
     */
    public void doLog (boolean log) {
        this.log = log;
    }

    /**
     * Is the Channel running ?
     * @return {boolean} true if still alive, false otherwise
     */
    public boolean isRunning() {
        return thread.isAlive();
    }

    /**
     * Set the identity of the ZMQ socket.
     * The identity is sent as a message preceding the other messages sent with socket.sendMore and socket.send
     * @param identity : a String representing the chosen identity
     */
    public void setIdentity(String identity) {
        this.identity = identity;
        this.socket.setIdentity(identity.getBytes());
    }

    /**
     * Set the behavior of the channel about storing messages history
     * @param b : true to store, false not to
     */
    public void doStoreHistory (boolean b) {
        // If user decide to start storing history and the history object hasn't been set yet, we set it
        if (b && history == null) history = new ArrayList<>();

        // Set the boolean that will be used to store history
        this.storeHistory = b;
    }

    /*==================================================================================================================
                                          CUSTOM METHODS FOR THIS CLASS ONLY
     =================================================================================================================*/

    /** Log all the messages received with their category name
     *
     * @param incomingMessage : complete Jupyter message. Look at the Jupyter doc to know more about it
     */
    private void logMessage (ArrayList<String> incomingMessage) {

        String msg = "\n------- MESSAGE RECEIVED ON " + name + " CHANNEL -------";

        // First, we verify that the message is as long as a common Jupyter message
        if (incomingMessage.size() == JUPYTER_MESSAGE_LENGTH) {
            // If so, we log it into the shell with prefix
            msg += "\nUUID : " + incomingMessage.get(0);
            msg += "\nDelimiter : " + incomingMessage.get(1);
            msg += "\nHmac : " + incomingMessage.get(2);
            msg += "\nHeader : " + incomingMessage.get(3);
            msg += "\nParent_header : " + incomingMessage.get(4);
            msg += "\nMetadata : " + incomingMessage.get(5);
            if (incomingMessage.get(6).length() < 1000) {
                msg += "\nContent : " + incomingMessage.get(6);
            }

            // Then we verify whether the message is a traceback message.
            // If so, we log the message differently to make it easier to read for the programmer.
            if(incomingMessage.get(0).indexOf("error") != -1) {
                JSONParser parser = new JSONParser();
                try {
                    JSONObject content = (JSONObject) parser.parse(incomingMessage.get(6));
                    JSONArray traceback = (JSONArray) content.get("traceback");
                    System.out.println(incomingMessage.get(6));
                    System.out.println("TRACEBACK");
                    for(int i=0; i<traceback.size(); i++) {
                        System.out.println(traceback.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {
            // If the message is not as long as a typical Jupyter message,
            // we log all the received data, without any prefix.
            for (String anIncomingMessage : incomingMessage) {
                msg += "\n" + anIncomingMessage;
            }
        }

        System.out.println(msg + "\n");
    }

    /** React depending on the received message
     *
     * @param incomingMessage : the received message, look at Jupyter doc to know its format
     */
    private void handleMessage(ArrayList<String> incomingMessage) {

        messagesManager.handleMessage(name, incomingMessage);
    }

    /**
     * Verify whether the given message is a correct UUID (Universally Unique IDentifier)
     * @param message {String} the message to test
     * @return {boolean} True if the message is a UUID, false otherwise.
     */
    private boolean isUuid (String message) {
        // Define the REGEX for an ip address
        String UUID_PATTERN = "(kernel)\\.([a-z-0-9-\\-]){36}\\.(.*)";

        // Create a pattern object used to run the regex
        Pattern pattern = Pattern.compile(UUID_PATTERN);

        // Create an object that verify whether the given "message" match the regex previously given
        Matcher matcher = pattern.matcher(message);

        // Run and return the test
        return matcher.matches();
    }

    /*==================================================================================================================
                                        METHODS IMPLEMENTED BY LOWER CLASSES
     =================================================================================================================*/

    /**
     * Define a initializeThread method that should be overridden by the child classes.
     *
     * This method is called when the Thread start in order to initialize what needs to be initialized.
     */
    protected abstract void initializeThread();
}
