package fr.irisa.diverse.Core;

import fr.irisa.diverse.FBPNetworkProtocol.FBPNetworkProtocolManager;
import fr.irisa.diverse.Flow.Flow;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

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

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    /** Start a new kernel. Should be used each time a new Simulation or Processing block is created
     *
     * @return : the uuid of the kernel
     */
    private String startNewKernel (String nodeId) {
        Kernel k = new Kernel();

        kernels.put(nodeId, k);

        return k.getContainerId();
    }

}
