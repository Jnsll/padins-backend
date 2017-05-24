package Core;

import Utils.Socket;
import org.json.simple.JSONObject;

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
    private Map<String, Kernel> kernels;
    private JSONObject flow = null;
    private Map<String, Socket> connectedClients = null;

    // Constructor
    public Workspace () {
        // Initialize attributes
        this.uuid = UUID.randomUUID().toString();
        this.kernels = new Hashtable<>();
        this.flow = new JSONObject();
        this.connectedClients = new Hashtable<>();
    }

    /*==================================================================================================================
                                              PUBLIC CLASS METHODS
     =================================================================================================================*/

    /** Store the newly connected client
     *
     * @param client : the socket of the client
     */
    public void newClientConnection (Socket client) {
        this.connectedClients.put(client.getId(), client);
    }

    /** Remove the reference to the client that has just disconnected
     *
     * @param client : the disconnected client
     */
    public void clientDeconnection (Socket client) {
        this.connectedClients.remove(client.getId());
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    /** Start a new kernel. Should be used each time a new Simulation or Processing block is created
     *
     * @return : the uuid of the kernel
     */
    private String startNewKernel () {
        Kernel k = new Kernel();

        kernels.put(k.getContainerId(), k);

        return k.getContainerId();
    }

}
