package fr.irisa.diverse.Core;

import fr.irisa.diverse.FBPNetworkProtocol.FBPNetworkProtocolManager;
import fr.irisa.diverse.Flow.Flow;
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
    private Map<String, FlowExecutionHandler> executionHandlers = null;
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
        this.executionHandlers = new Hashtable<>();
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
        // Check if an execution handler is associated to this graph
        executionHandlers.computeIfAbsent(graph, k -> new FlowExecutionHandler(graph, this, this.flow));
        FlowExecutionHandler executionHandler = executionHandlers.get(graph);

        executionHandler.run();
    }

    public void stopGraph (String graph) {
        // Check if an execution handler is associated to this graph
        FlowExecutionHandler executionHandler = executionHandlers.get(graph);

        executionHandler.stop();
    }

    public boolean graphRunning (String graph) {
        // Check if an execution handler is associated to this graph
        FlowExecutionHandler executionHandler = executionHandlers.get(graph);

        if (executionHandler == null) return false;
        else return executionHandler.isRunning();
    }

    public void executeNode (Node node) {
        // If the node is running, we wait for it to stop
        while(isNodeRunning(node.getId())){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Then we launch the execution
        // TODO
    }

    public void stopNode (Node node) {
        // We do it only if the node is running. Otherwise, it is not necessary.
        if (isNodeRunning(node.getId())) {
            // TODO
        }
    }

    public boolean isNodeRunning (String nodeId) {
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



    /* =================================================================================================================
                                                    EXCEPTION CLASSES
       ===============================================================================================================*/

    public class NotExistingGraphException extends Exception {
        public NotExistingGraphException(String graph) {
            super("[ERROR] Impossible to run the graph " + graph + ", because it doesn't exist");
        }
    }

}
