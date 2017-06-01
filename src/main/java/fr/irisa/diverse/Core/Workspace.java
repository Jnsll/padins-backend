package fr.irisa.diverse.Core;

import fr.irisa.diverse.FBPNetworkProtocol.FBPNetworkProtocolManager;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Group;
import fr.irisa.diverse.Flow.Node;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.util.*;

/** The workspace is the central element of this project.
 *
 * A workspace corresponds to one project, so one flow.
 * It can have several kernels and connected users.
 * One flow is represented by one JSON, containing all the vue structure.
 *
 * Created by antoine on 25/05/2017.
 */
public class Workspace {

    // Attributes
    public String uuid = null;
    private int socketPort;
    private String name = "";
    private Map<String, Kernel> kernels;
    private Flow flow = null;
    private Map<String, Session> connectedClients = null;
    private FBPNetworkProtocolManager clientCommunicationManager = null;
    private String library = "hydro-geology";
    public final String RUNTIME_TYPE = "Computational Science";

    // Constructor
    public Workspace (String name, int socketPort) {
        // Initialize attributes
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.kernels = new Hashtable<>();
        this.flow = new Flow(this);
        this.connectedClients = new Hashtable<>();
        this.socketPort = socketPort;
        this.clientCommunicationManager = new FBPNetworkProtocolManager(this);
    }

    /*==================================================================================================================
                                              PUBLIC CLASS METHODS
     =================================================================================================================*/

    /** Store the newly connected client
     *
     * @param client : the socket of the client
     */
    public void newClientConnection (Session client) {
        this.connectedClients.put(client.getId(), client);
    }

    /** Remove the reference to the client that has just disconnected
     *
     * @param client : the disconnected client
     */
    public void clientDeconnection (Session client) {
        this.connectedClients.remove(client.getId());
    }

    /** Start a new kernel. Should be used each time a new Simulation or Processing block is created
     *
     * @return : the uuid of the kernel
     */
    public String startNewKernel (String nodeId) {
        Kernel k = new Kernel();

        kernels.put(nodeId, k);

        return k.getContainerId();
    }

    /** Stop a running kernel. Commonly used when a node is removed
     *
     * @param nodeId : the nodeId linked to the kernel
     */
    public void stopKernel (String nodeId) {
        Kernel kernel = kernels.get(nodeId);
        if(kernel != null) kernel.stop();
    }

    /**
     * Stop all the kernels.
     * Use only when all users have stopped the connexion or when you stop the server.
     */
    public void stopKernels () {
        Set keys = kernels.keySet();
        Iterator iterator = keys.iterator();

        while(iterator.hasNext()){
            Kernel k = kernels.get(iterator.next());
            k.stop();
        }
    }

    public void startGraph (String graph) throws NotExistingGraphException {
        // First retrieve the graph.
        Object g = flow.getGraph(graph);

        // Now there are two cases : the graph is the Flow or it is a Group.
        if (g instanceof Flow) {
            run();
        } else if (g instanceof Group) {
            runGroup((Group) g);
        }
    }

    public boolean graphRunning (String graph) {
        // TODO
        return false;
    }

    /*==================================================================================================================
                                                GETTERS AND SETTERS
     =================================================================================================================*/

    public MessageHandler getMessageHandler (Session session) {
        clientCommunicationManager.setSession(session);
        return clientCommunicationManager;
    }

    public String getName () {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Session> getConnectedClients() {
        return connectedClients;
    }

    public String getLibrary() {
        return library;
    }

    public String getUuid() {
        return uuid;
    }

    public Flow getFlow() {
        return flow;
    }

    public FBPNetworkProtocolManager getClientCommunicationManager() {
        return clientCommunicationManager;
    }

    public Kernel getKernel (String nodeId) {
        return kernels.get(nodeId);
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    private boolean isNodeRunning (String nodeId) {
        // First retrieve the node
        Node n = flow.getNode(nodeId, uuid);

        // If the node is not executable, obviously it is not running
        if (!n.isExecutable()) return false;
        // Elsewhere we check if running. Checking whether the node is running is finally checking if its associated
        // kernel is busy.
        else {
            Kernel k = kernels.get(nodeId);
            return k.isBusy();
        }
    }

    private void run () {
        // TODO
        // Must store when it started running
        // First : retrieve the nodes to execute in the right order
        // Then : run each block one by one. Giving to the method : the block to execute, its src and tgt
        // Must store that this graph is running
    }

    private void runGroup (Group group) {
        // TODO
        // Must store when it started running
        // Must do the same as run but for a group
        // Must store that the graph is running
    }

    private ArrayList<Node> getExecutableNodesOrdered () {
        // TODO
        return null;
    }

    /* =================================================================================================================
                                                    EXCEPTION CLASSES
       ===============================================================================================================*/

    public class NotExistingGraphException extends Exception {
        public NotExistingGraphException(String graph) {
            super("[ERROR] Impossible to run the graph " + graph + ", because it doesn't exist");
        }
    }

}
