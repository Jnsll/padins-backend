package fr.irisa.diverse.Core;

import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPNetworkProtocolManager;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Node;

import fr.irisa.diverse.Webserver.Servlets.WebsocketOthers.ServerSocket;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private ArrayList<ServerSocket> connectedClients = null;
    public FBPNetworkProtocolManager clientCommunicationManager = null;
    private Map<String, FlowExecutionHandler> executionHandlers = null;
    private String library = "hydro-geology";
    public final String RUNTIME_TYPE = "Computational Science";
    private final String pathToWorkspacesStorage = Root.PATH_TO_PROJECT_STORAGE + "/workspaces/";
    private Path pathToWorkspaceFolder;
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
        this.pathToWorkspaceFolder = Paths.get(URI.create("file:///" + pathToWorkspacesStorage + uuid));

        if (createFolder(this.pathToWorkspaceFolder)) {
            // If the workspace's folder didn't exist we have created it.
            // So no flow.json existed, we create a new flow and a new UUID
            this.flow = new Flow(this);
            this.save();
        } else {
            // Otherwise we import the existing flow
            JSONObject flowJSON = importFlowJSON(this.pathToWorkspaceFolder);
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
    public void newClientConnection (ServerSocket client) {
        this.connectedClients.add(client);
    }

    /** Remove the reference to the client that has just disconnected
     *
     * @param client : the disconnected client
     */
    public void clientDeconnection (ServerSocket client) {
        this.connectedClients.remove(client);
    }

    /** Start a new kernel. Should be used each time a new Simulation or Processing block is created
     *
     * @return : the uuid of the kernel
     */
    public void startNewKernel (String nodeId) {
        Runnable task = () -> {
                Kernel k = new Kernel(nodeId, this);

                kernels.put(nodeId, k);
        };

        Thread thread = new Thread(task);
        thread.start();

    }

    /** Stop a running kernel. Commonly used when a node is removed
     *
     * @param nodeId : the nodeId linked to the kernel
     */
    public void stopKernel (String nodeId) {
        Runnable task = () -> {
            Kernel kernel = kernels.get(nodeId);
            if(kernel != null) kernel.stop();
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Stop all the kernels.
     * Use only when all users have stopped the connexion or when you stop the server.
     */
    public boolean stopKernels () throws InterruptedException {
        // Retrieve all the kernels started on the workspace
        Set keys = kernels.keySet();
        Iterator iterator = keys.iterator();

        // Create an executor service to launch the stop of every kernels in the same time and wait for every one
        // of them to be stopped.
        ExecutorService es = Executors.newCachedThreadPool();

        while(iterator.hasNext()){
            // Create the stop task for each kernel
            Kernel k = kernels.get(iterator.next());
            Runnable task = () -> {
                k.stop();
            };

            // Execute the stop task via the executor service
            es.execute(task);
        }

        es.shutdown();
        return es.awaitTermination(3, TimeUnit.MINUTES);
    }

    /**
     * Start the execution of a given graph.
     *
     * @param graph : the id of the graph. Can be the full flow or a group.
     * @throws NotExistingGraphException
     */
    public void startGraph (String graph) throws NotExistingGraphException {
        // Check if an execution handler is associated to this graph
        executionHandlers.computeIfAbsent(graph, k -> new FlowExecutionHandler(graph, this, this.flow));
        FlowExecutionHandler executionHandler = executionHandlers.get(graph);

        executionHandler.run();
    }

    /**
     * Stop a running graph
     *
     * @param graph : the id of the graph. Can be the full flow or a group.
     */
    public void stopGraph (String graph) {
        // Check if an execution handler is associated to this graph
        FlowExecutionHandler executionHandler = executionHandlers.get(graph);

        executionHandler.stop();
    }

    /**
     * Is a graph running ?
     *
     * @param graph : the id of the graph. Can be the full flow or a group.
     * @return true if the execution of the graph is running.
     */
    public boolean graphRunning (String graph) {
        // Check if an execution handler is associated to this graph
        FlowExecutionHandler executionHandler = executionHandlers.get(graph);

        if (executionHandler == null) return false;
        else return executionHandler.isRunning();
    }

    /**
     * Launch the execution of a given node.
     *
     * @param node : the node to execute.
     */
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
            k.executeCode(code, node);
        }
    }

    /**
     * Broadcast the information that the given node throws an error while executing in the kernel.
     *
     * @param nodeId the id of the node.
     */
    public void errorExecutingNode (String nodeId) {
        Node node = flow.getNode(nodeId, flow.getId());
        node.errorOccurred();

        Set<String> keys = executionHandlers.keySet();
        Iterator i = keys.iterator();

        while (i.hasNext()) {
            executionHandlers.get(i.next()).errorExecutingNode(node);
        }
    }

    /**
     * Stop the execution of a given node.
     *
     * @param node : the node to stop.
     */
    public void stopNode (Node node) {
        // We do it only if the node is running. Otherwise, it is not necessary.
        if (isNodeRunning(node.getId())) {
            Kernel k = kernels.get(node.getId());
            k.stopExecution();
        }
    }

    /**
     * Is a given node running ?
     *
     * @param nodeId : the id of the node
     * @return True if the node is running
     */
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

    /**
     * Handle an error returned by the Jupyter kernel.
     * It sends an error message to the connected UIs.
     *
     * @param error : the error sent by the kernel.
     */
    public void errorFromKernel (String error) {
        clientCommunicationManager.sendErrorToAll("NETWORK", "[ERROR JUPYTER] " + error);
    }

    /**
     * Save the workspace, storing the full flow as a json file in the workspace folder on the HD.
     * Each workspace has its own folder on the HD.
     */
    public void save () {
        // Make sure the workspace folder exist
        createFolder(this.pathToWorkspaceFolder);

        // Write the serialized Flow object
        System.out.println("Saving");
        try (FileWriter file = new FileWriter(this.pathToWorkspaceFolder.toString() + "/" + FLOW_FILE_NAME)) {
            file.write(flow.serialize());
            System.out.println("Successfully Copied Flow " + flow.getId() + " to File :)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to the UI containing the modifications done on the given node.
     * @param node : the node for which you want to send the information to the UIs.
     */
    public void sendUpdateNodeMessage (Node node) {
        this.clientCommunicationManager.sendUpdateNodeMessage(node);
    }

    /*==================================================================================================================
                                                GETTERS AND SETTERS
     =================================================================================================================*/

    /**
     * Get the name of the workspace. The name is defined by the end-user.
     * @return the name of the workspace.
     */
    public String getName () {
        return this.name;
    }

    /**
     * Change the name of the workspace to the given value.
     * @param name the new name of the workspace
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the list of the connected clients.
     * @return the list of connected clients as an array of ServerSocket.
     */
    public ArrayList<ServerSocket> getConnectedClients() {
        return connectedClients;
    }

    /**
     * Get the library of components used in this workspace.
     *
     * @return the name of the library.
     */
    public String getLibrary() {
        return library;
    }

    /**
     * Get the unique id of the workspace.
     *
     * @return the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the flow designed by the user. The flow is the bunch of components (nodes), linked with edges that
     * described the process the user wants to study/simulate.
     *
     * @return the Flow instance
     */
    public Flow getFlow() {
        return flow;
    }

    public Kernel getKernel (String nodeId) {
        return kernels.get(nodeId);
    }

    /**
     * Get the absolute path to the workspace folder on the machine.
     *
     * @return the path of the workspace
     */
    public Path getPathToWorkspaceFolder() {
        return pathToWorkspaceFolder;
    }

    /**
     * Get the node associated to a kernel
     * @param k the kernel
     * @return the id of the node
     */
    public String getNodeIdForKernel (Kernel k) {
        Set<String> keys = kernels.keySet();
        for (String key: keys) {
            if (kernels.get(key).getContainerId().equals(k.getContainerId())) { return key; }
        }

        return "";
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    /**
     * Import a flow as a JSONObject from the given folder. The flow file must be named flow.json
     *
     * @param pathToFolder the path to the folder containing the flow.
     * @return a JSONObject containing the flow.
     */
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

    /**
     * Create the folder of the workspace.
     *
     * @param path the path to folder in which you want to create the folder.
     * @return True if the folder exists after this method run.
     */
    private boolean createFolder (Path path) {
        if (Files.notExists(path)) {
            // If the folder doesn't already exists we create it
            File f = new File(path.toString());
            try {
                // Create the folder
                f.mkdir();
                // Create an empty flow.json file
                f = new File(path + "/flow.json");
                f.createNewFile();
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

    /**
     * Exception indicating that the graph to run doesn't exist.
     */
    public class NotExistingGraphException extends Exception {
        public NotExistingGraphException(String graph) {
            super("[ERROR] Impossible to run the graph " + graph + ", because it doesn't exist");
        }
    }

}
