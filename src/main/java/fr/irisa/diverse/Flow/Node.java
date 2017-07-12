package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * A node correspond to a block on a flow-based program
 * A node is a part of a flow.
 *
 * To know more about Flow-Based programming : http://www.jpaulmorrison.com/fbp/concepts.html
 * To see the FBP Network Protocol : https://flowbased.github.io/fbp-protocol/#graph-addnode
 *
 * Created by antoine on 29/05/17.
 */
public class Node implements Comparable<Node>{

    // Attributes related to outer objects
    private Flow owningFlow = null;

    // The JSONObject containig all the data of a node
    private JSONObject node = null;

    // Attributes that compose the content of the JSON used for sharing a node to other services
    private String id = "";
    private String component = "";
    private JSONObject metadata = null;
    private String graph = null;
    private Ports inports = null;
    private Ports outports = null;

    // Other information about a node
    private boolean executable;
    private JSONObject pickledResult = null; // A pickle is a string for the python serializer pickle

    // Information about the runs of a node
    private long lastRun;
    private long lastModification;
    private long lastError;
    private Date date;

    /* =================================================================================================================
                                                CONSTRUCTOR
       ===============================================================================================================*/

    public Node (String id, String component, JSONObject metadata, String graph, boolean executable, Flow owningFlow) {
        this.node = new JSONObject();
        this.owningFlow = owningFlow;
        this.component = component;
        this.metadata = metadata;
        this.graph = graph;
        this.inports = ComponentsUtils.getInPortsForComponent(owningFlow.getComponentsLibrary(), component, id);
        this.outports = ComponentsUtils.getOutPortsForComponent(owningFlow.getComponentsLibrary(), component, id);
        this.id = id;
        this.executable = executable;
        this.date = new Date();
        this.lastModification = date.getTime();
        this.lastRun = 0;
        this.lastError = 0;

        // Start a kernel if needed
        if (executable) {
            owningFlow.owningWorkspace.startNewKernel(id);
        }

    }

    /* =================================================================================================================
                                             GETTERS AND SETTERS FUNCTIONS
       ===============================================================================================================*/

    /**
     * Get the unique id of the node.
     * @return {String} the uuid of the node
     */
    public String getId (){
        if (id == null) id = "";

        return id;
    }

    /**
     * Update the id of the node. The ID must be unique !
     * @param newId {String} the new uuid of the node
     */
    public void setId (String newId) { id = newId; }

    /**
     * Get the inports of the node.
     * @return {Ports} the inports of the node
     */
    public Ports getInports() {
        return inports;
    }

    /**
     * Get the outports of the node.
     * @return {Ports} te outports of the node
     */
    public Ports getOutports() {
        return outports;
    }

    /**
     * Get the metadata of the node.
     * @return {JSONObject} the metadata of the node
     */
    public JSONObject getMetadata() {
        return metadata;
    }

    /**
     * Set the metadata of the node
     * @param metadata {JSONObject} the new metadata. Must contain all the metadata, not only the new ones
     */
    public void setMetadata(JSONObject metadata) {
        nodeUpdated();
        this.metadata = metadata;
    }

    /**
     * Get the id of the graph the node is into. The graph can be the root Flow or a group.
     * @return {String} the id of the graph
     */
    public String getGraph() {
        return graph;
    }

    /**
     * Get the component on which this node is based.
     * The component name is formatted as : {{Library}}/{{ComponentName}}
     * @return {String} the name of the component with its library name
     */
    public String getComponent() {
        return component;
    }

    /**
     * Get the JSON object containing all the data of the node. Usually used to send the node data to another service.
     * @return {JSONObject} the node's json object
     */
    public JSONObject getJson() {
        build();
        return node;
    }

    /**
     * Is the node executable ?
     * @return {boolean} true if executable, no elsewere
     */
    public boolean isExecutable () {
        return executable;
    }

    /**
     * Get the python code of the node. Will be null if not executable.
     * @return {String} the code of the node. Only python support for now.
     */
    public String getCode () {
        return metadata.get("code") == null ? "" : (String) metadata.get("code");
    }

    /**
     * Get the result of the node's execution if executable, pickle formatted. Otherwise all the data that it contains.
     * Usually used to inject the data in the beginning of the next nodes for its execution.
     *
     * @return {JSONObject} containing all the data with a key: value format
     */
    public JSONObject getResult () {
        if (isExecutable()) return getPickledResult();
        else return getJsonResult();
    }

    /**
     * Get the data of the node as a JSON formatted as key: value pairs.
     * @return {JSONObject} containing data as key: value
     */
    public JSONObject getJsonResult() {
        return (JSONObject) this.metadata.get("result");
    }

    /**
     * Get the data of the node as pairs of key: pickle.
     * A pickle is a string that is the result of the serialization of any variable in python using pickle.dumps.
     * -> See python documentation.
     * @return {JSONObject} key: pickle pairs
     */
    public JSONObject getPickledResult() { return pickledResult != null ? pickledResult : new JSONObject(); }

    /**
     * Set the pickle result object.
     * A pickle is a string that is the result of the serialization of any variable in python using pickle.dumps.
     * -> See python documentation.
     * @param result {JSONObject} Must be formatted as key: pickle pairs
     */
    public void setPickledResult (JSONObject result) {
        pickledResult = result;
    }

    /**
     * Set the result object, containing all the data to transfer to the nodes connected to the outports.
     * @param result {JSONObject} Must be formatted as key: value pairs with value being a stringify json
     */
    public void setJsonResult(JSONObject result) {
        this.metadata.put("result", result);
        date = new Date();
        lastRun = date.getTime();

        sendUpdateNodeMessage();
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        build();
        // Return it as a String
        return node.toJSONString();
    }

    /**
     * Store the information that an edge has been connected to a port of the node.
     * @param port {String} the name of the port on which the edge has been connected
     * @param edge {String} the id of the connected edge
     */
    public void assignPortToEdge (String port, String edge) {
        // Generate an event saying that the node has been updated
        nodeUpdated();

        // Retrieve the Port instance corresponding to the given port name
        Port p = findPort(port);
        if (p != null) {
            // Connect the edge to the port
            p.addConnectedEdge(edge);
        }
    }

    /**
     * Remove the association between the port of the node and the edge
     * @param port {String} the name of the port on which the edge has been disconnected
     * @param edge {String} the id of the disconnected edge
     */
    public void unassignPortToEdge (String port, String edge) {
        // Generate an event saying that the node has been updated
        nodeUpdated();

        // Retrieve the Port instance corresponding to the given port name
        Port p = findPort(port);
        if (p != null) {
            // Disconnect the edge from the port
            p.removeConnectedEdge(edge);
        }
    }

    /**
     * Retrieve the data of all the ports connected to the inports of this node.
     * @return {JSONObject} containing the data (variables ) as key:pickle pairs and the key:stringified-json pairs
     */
    public JSONObject getPreviousNodesData() {
        // Create the result json that will contain the pickled and jsonified key:value pairs
        JSONObject res = new JSONObject();
        JSONObject pickled = new JSONObject();
        JSONObject jsonified = new JSONObject();

        // Retrieve the list of nodes connected on the inports
        ArrayList<Node> previousNodes = previousInFlow();

        if (previousNodes != null) {
            // Retrieve of pickled and jsonified data for each connected node.
            for(int i=0; i<previousNodes.size(); i++) {
                JSONObject data = previousNodes.get(i).getResult();

                if (data != null) {
                    /* If the previous node is executable, we know that the data will be pickle formatted.
                     * Otherwise we will retrieve JSON data
                     * This information will be used later in order to inject the data into the code of the next nodes.
                     */
                    if (previousNodes.get(i).isExecutable()) {
                        // If executable,
                        // take each key => value pair in data and put it into pickle
                        Iterator iterator = data.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            pickled.put(key, data.get(key));
                        }

                    } else {
                        // If not executable
                        // Take each key => value pair in data and put it into json
                        Iterator iterator = data.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            jsonified.put(key, data.get(key));
                        }
                    }
                }
            }
        }

        /* Now that we have retrieved and separated all the pickled and jsonified data, we can store them
         * in the result JSONObject.
         */
        res.put("jsonified", jsonified);
        res.put("pickled", pickled);

        return res;
    }

    /**
     * Retrieve the list of nodes connected to the inports.
     * @return {ArrayList<Node>} the list of node connected to the inports
     */
    public ArrayList<Node> previousInFlow () {
        return nextOrPreviousNodeInFlow(getInports());
    }

    /**
     * Retrieve the list of nodes connected to the outports.
     * Usually used in order to know what are the next nodes to execute when running the flow.
     * @return {ArrayList<Node>} the list of node connected to the outports
     */
    public ArrayList<Node> nextInFlow () {
        /* In order to avoid running the next nodes if this node thrown an error on the last run,
         * we verify that the user modified his/her code after the last thrown error.
         * It he/she didn't, we return an empty list.
         */
        if (lastModification > lastError) {
            return nextOrPreviousNodeInFlow(getOutports());
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * Is the node running ?
     * @return {boolean} True if running, False if not
     */
    public boolean isRunning () {
        return owningFlow.owningWorkspace.isNodeRunning(getId());
    }

    /**
     * Has the node finished running ?
     * @return {boolean} True if run finished, False if still running
     */
    public boolean hasFinished () {
        return !isRunning();
    }

    /**
     * Should the node be re-run ?
     * If this node and this node's previous nodes in the flow haven't been modified since the last run
     * it is not necessary to run it again, the result will be exactly the same.
     *
     * To make sure this is reliable, it will verify that all nodes connected to the inports of this node haven't been
     * modified, and their own previous node that will themselves verified that their previous node haven't been modified
     * and so on, recursively.
     * @return
     */
    public boolean shouldBeReRun () {
        // First and obvious verification : if the node is not executable, we won't run it.
        if (!isExecutable()) {
            return false;
        } else {
            // Otherwise, step by step we verify what's explained above.

            // Has this node been modified since last run ?
            boolean res = lastRun < lastModification;

            // Have this node's previous nodes in flow been modified (including input data) since last run.
            ArrayList<Node> previousNodes = previousInFlow();

            if(previousNodes != null) {
                for (Node previous : previousNodes) {
                    res = res || previous.lastModification > this.lastRun;
                }
            }

            return  res;
        }

    }

    /**
     * Has this node a know error ?
     * We know that the node as an error if an error has been thrown during this node's last run.
     * Then we check if the user modified his/her code since the last error. If he/she does, we suppose that
     * he/she corrected the error.
     * @return {boolean} True if we are sure that there is an error in the code.
     */
    public boolean noKnownError () {
        return lastError < lastModification;
    }

    /**
     * Prepare the node for the execution, stopping it if it is already running.
     */
    public void prepareForExecution () {
        owningFlow.owningWorkspace.stopNode(this);
    }

    /**
     * Has the node received a result after the given time ?
     * @param time {long} the timestamp after which you want to know if the node received a result
     * @return {boolean} true if the node received the result of its execution after the given time.
     */
    public boolean receivedResultAfterTime (long time) {
        return lastRun > time;
    }

    /**
     * Method used to prevent this node that an error occurred during its execution.
     */
    public void errorOccurred() {
        this.lastError = new Date().getTime();
    }

    @Override
    public int compareTo(Node o) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        //
        // To do that, we use the ids.
        return o.getId().compareTo(id);
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    /**
     * Build the json object representing the node.
     * The json is used to send all the information of this group to any other service.
     *
     * The JSONObject contains :
     * - id {String}
     * - component {String}
     * - metadata {JSONObject}
     * - graph {String}
     * - inports {JSONObject}
     * - outports {JSONObject}
     */
    private void build () {
        // Build the node JSON
        node.put("id", getId());
        node.put("component", getComponent());
        node.put("metadata", getMetadata());
        node.put("graph", getGraph());
        node.put("inports", getInports().toJson());
        node.put("outports", getOutports().toJson());
    }

    /**
     * Get a Port from its name
     * @param name {String} the name of the port, no matter if it's an inport or outport
     * @return {Port} the port with the given name, null if not found
     */
    private Port findPort (String name) {
        // First search into inports
        Port res = findPortInGivenObject(inports, name);
        // If not found into inports, search into outports
        if (res == null) res = findPortInGivenObject(outports, name);

        // Return the result
        return res;
    }

    /**
     * Get a Port with the given name in the given Ports object
     * @param ports {Ports} the given ports where to search for the port with the given name
     * @param name {String} the name of the searched port
     * @return {Port} the port found, null if not found
     */
    private Port findPortInGivenObject (Ports ports, String name) {
        for(int i=0; i<ports.size(); i++) {
            Port p = ports.get(i);
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    /**
     * Give all the nodes connected to the opposite side of the edges connected to the given ports.
     * The given ports must be either the inports of this block or the ouports.
     * @param ports {Ports} the ports you want to get the nodes connected to the opposite of the edges
     * @return {ArrayList<Node>} the list of nodes connected to the opposite of the edges connected to the given ports
     */
    private ArrayList<Node> nextOrPreviousNodeInFlow (Ports ports) {
        // Create the object that will be returned
        ArrayList<Node> res = new ArrayList<>();
        boolean previous = ports == getInports();

        // Search the previous or next nodes for each ports in ports an add them into res
        for (int i=0; i<ports.size(); i++) {
            ArrayList<Node> n = oppositeNodesForPort(ports.get(i), previous);
            if (n != null) {
                for(int j=0; j<n.size(); j++) {
                    res.add(n.get(j));
                }
            }
        }

        // End
        return res.size() == 0 ? null : res;
    }

    /**
     * Get the nodes connected to the opposite side of the edges connected to the given port.
     * Choose between getting the nodes at the beginning or at the end of the edges.
     * @param p {Port} the port used to retrieve the connected edges and their src or tgt nodes
     * @param previousNode {boolean} do you want to previous nodes or next nodes ?
     *                     Using the flow execution order as the reference for previous or next.
     * @return {ArrayList<Node>} the list of nodes on the opposite of the port
     */
    private ArrayList<Node> oppositeNodesForPort(Port p, boolean previousNode) {
        // First : retrieve the edge
        ArrayList<String> edgesIds = p.getConnectedEdgesId();
        ArrayList<Node> oppositeNodes = new ArrayList<>();

        for (int i=0; i<edgesIds.size(); i++) {
            Edge e = owningFlow.getEdge(edgesIds.get(i));
            // Second : determine whether we have to return the src or tgt node of this edge
            String resNodeId = (String) (previousNode ? e.getSrc().get("node") : e.getTgt().get("node"));
            // Add the node into the list
            oppositeNodes.add(owningFlow.getNode(resNodeId, owningFlow.getId()));
        }


        // Finally, if there is no edge we return null
        if(oppositeNodes.size() == 0) return null;

        return oppositeNodes;
    }

    /**
     * Send an updatenode message to the UIs connected to the workspace this node is on.
     */
    private void sendUpdateNodeMessage () {
        owningFlow.owningWorkspace.sendUpdateNodeMessage(this);
    }

    /**
     * React to a nodeUpdated event.
     * The reaction is simply storing the timestamp of the last modification of this node.
     */
    private void nodeUpdated () {
        date = new Date();
        lastModification = date.getTime();
    }
}
