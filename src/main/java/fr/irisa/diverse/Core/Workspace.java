package fr.irisa.diverse.Core;

import fr.irisa.diverse.FBPNetworkProtocol.FBPNetworkProtocolManager;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Node;

import javax.websocket.MessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    private String name = "";
    private Map<String, Kernel> kernels;
    private Flow flow = null;
    private ArrayList<Session> connectedClients = null;
    private FBPNetworkProtocolManager clientCommunicationManager = null;
    private Map<String, FlowExecutionHandler> executionHandlers = null;
    private String library = "hydro-geology";
    public final String RUNTIME_TYPE = "Computational Science";
    private final String pathToWorkspacesStorage = Workspace.class.getClassLoader().getResource("workspaces/").getPath();
    private Path pathToFolder;
    private final String FLOW_FILE_NAME = "flow.json";

    // Constructor
    public Workspace (String name, String id) {
        // Initialize attributes
        this.uuid = id == null ? UUID.randomUUID().toString() : id;
        this.name = (name != null) ? name : "";
        this.kernels = new Hashtable<>();
        this.connectedClients = new ArrayList<>();
        this.executionHandlers = new Hashtable<>();

        // Create a folder for this workspace if not already existing
        this.pathToFolder = Paths.get(URI.create("file:///" + pathToWorkspacesStorage + uuid));

        if (createFolder(this.pathToFolder)) {
            // If the workspace's folder didn't exist we have created it.
            // So no flow.json existed, we create a new flow and a new UUID
            this.flow = new Flow(this);
        } else {
            // Otherwise we import the existing flow
            JSONObject flowJSON = importFlowJSON(this.pathToFolder);
            if (flowJSON != null) {
                this.flow = new Flow(flowJSON, this);
                this.name = (String) flowJSON.get("name");
            } else {
                this.flow = new Flow(this);
            }
        } // End of creating flow var


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
        this.connectedClients.add(client);
    }

    /** Remove the reference to the client that has just disconnected
     *
     * @param client : the disconnected client
     */
    public void clientDeconnection (Session client) {
        this.connectedClients.remove(client);
    }

    /** Start a new kernel. Should be used each time a new Simulation or Processing block is created
     *
     * @return : the uuid of the kernel
     */
    public String startNewKernel (String nodeId) {
        Kernel k = new Kernel(nodeId, this);

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
        if (node.isExecutable()) {
            // If the node is running, we wait for it to stop
            while(isNodeRunning(node.getId())){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Then we launch the execution
            String code = node.getCode();
            Kernel k = kernels.get(node.getId());
            k.executeCode(code);
        }
    }

    public void stopNode (Node node) {
        // We do it only if the node is running. Otherwise, it is not necessary.
        if (isNodeRunning(node.getId())) {
            Kernel k = kernels.get(node.getId());
            k.stopExecution();
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

    public void errorFromKernel (String error) {
        clientCommunicationManager.sendErrorToAll("NETWORK", "[ERROR JUPYTER] " + error);
    }

    public void save () {
        // Make sure the workspace folder exist
        createFolder(this.pathToFolder);

        // Write the serialized Flow object
        try (FileWriter file = new FileWriter(this.pathToFolder.toString() + "/" + FLOW_FILE_NAME)) {
            file.write(flow.serialize());
            System.out.println("Successfully Copied Flow " + flow.getId() + " to File...");
        } catch (IOException e) {
            e.printStackTrace();
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

    public ArrayList<Session> getConnectedClients() {
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

    private JSONObject importFlowJSON (Path pathToFolder) {
        // Create a JSONParser to parse the content of the file
        JSONParser parser = new JSONParser();

        // The JSONObject instance that will be returned
        JSONObject flow;

        try{
            // Read and parse the json file
            flow = (JSONObject) parser.parse(new FileReader(pathToFolder.toString() + "/" + FLOW_FILE_NAME));
            // If the file has been found and parsed we return it as a JSONObject
            return flow;
        } catch (ParseException | IOException e) {
            // If the file hasn't been found we return null
            e.printStackTrace();
            return null;
        }
    }

    private boolean createFolder (Path path) {
        if (Files.notExists(path)) {
            // If the folder doesn't already exists we create it
            File f = new File(path.toString());
            try {
                // Create the folder
                f.mkdir();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
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
