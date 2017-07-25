package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A Port is either an input or output of a Node. It offers the possibility to connect nodes together.
 *
 * For a functional purpose, a port has a name and a port. The name is the label that should be displayed,
 * and the port correspond to the name used to differentiate two ports.
 *
 * We use several inports or outports to offer different connexion functionality.
 * For example, a node Addition could have to inports A and B and one outport sum. A and B are used to differentiate the
 * two data to sum, and the outport is used to transmit the result to another node. We can link it to another sum node
 * and sum the result with another B data. An so on.
 *
 * Created by antoine on 29/05/17.
 */
public class Port {

    // Attributes
    private JSONObject portJSON = null;
    private String id = "";
    private String name = "";
    private String port = "";
    private String node = "";
    private JSONObject metadata = null;
    private ArrayList<String> connectedEdges;
    private final String type = "Object";

    /* =================================================================================================================
                                                CONSTRUCTOR
       ===============================================================================================================*/
    Port (String port, String name) {
        id = UUID.randomUUID().toString();
        this.portJSON = new JSONObject();
        this.port = port;
        this.name = name;
        this.metadata = new JSONObject();
        this.connectedEdges = new ArrayList<>();
    }

    Port (String port, String name, JSONObject metadata) {
        this(port, name);
        this.metadata = metadata;
    }

    Port (String port, String name, JSONObject metadata, ArrayList<String> connectedEdges) {
        this(port, name, metadata);

        this.connectedEdges = connectedEdges;
    }

    /* =================================================================================================================
                                             GETTERS AND SETTERS FUNCTIONS
       ===============================================================================================================*/

    /**
     * Get the id of the Port
     * @return {String} the id of the port
     */
    public String getId() {
        return id;
    }

    /**
     * Get the port name, the one used by the program to differentiate ports.
     * @return {String} the port's name
     */
    public String getPort () {
        if (port == null) port = "";

        return port;
    }

    /**
     * Get the metadata of the port.
     * @return {JSONObject} the metadata
     */
    public JSONObject getMetadata () {
        if (metadata == null) metadata = new JSONObject();

        return metadata;
    }

    /**
     * Get the connected edges'id of the port.
     *
     * This method can be used to make easier retrieving the nodes connected to the opposite of the edge
     * connected to this port.
     *
     * Several edges can be connected on one port.
     * @return {ArrayList<String>} the list of the ids of the edges connected to the port.
     */
    public ArrayList<String> getConnectedEdgesId() {
        if (connectedEdges == null) connectedEdges = new ArrayList<>();

        return connectedEdges;
    }

    /**
     * Get the type of data that can be connected to this port.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Get the public name of the port, the one to display on the UIs.
     * @return {String} the name of the port.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the node on which this port is.
     * @return {Node} the node containing the port.
     */
    public String getNode() {
        return node;
    }

    /**
     * Set the node containing this port.
     * @param node {Node} the new node containing this port.
     */
    public void setNode(String node) {
        this.node = node;
    }

    /**
     * Set the list of edges connected to this port.
     *
     * @param connectedEdges {ArrayList<String>} the list of the ids of the edges connected to this port.
     */
    public void setConnectedEdges(ArrayList<String> connectedEdges) {
        this.connectedEdges = connectedEdges;
    }

    /**
     * Connect a new edge to this port.
     * @param edgeId {String} the id of the edge.
     */
    public void addConnectedEdge (String edgeId) {
        if (this.connectedEdges.indexOf(edgeId) == -1) {
            this.connectedEdges.add(edgeId);
        }
    }

    /**
     * Disconnect an edge from this port
     * @param edgeId {String} the id of the edge
     */
    public void removeConnectedEdge (String edgeId) {
        if (this.connectedEdges.indexOf(edgeId) != -1) {
            this.connectedEdges.remove(this.connectedEdges.indexOf(edgeId));
        }
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        build();
        // Return it as a String
        return portJSON.toJSONString();
    }

    public JSONObject toJson() {
        build();
        return portJSON;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    /**
     * Build the port object in order to create a JSON that can be serialized and sent.
     *
     * The Port object JSONObject contains :
     * - id {String}
     * - public {String}
     * - node {String}
     * - port {String}
     * - metadata {JSONObject}
     * - connectedEdges {List<String>}
     * - type {String}
     */
    private void build () {
        // Build the port JSON
        portJSON.put("id", getId());
        portJSON.put("public", getName());
        portJSON.put("node", getNode());
        portJSON.put("port", getPort());
        portJSON.put("metadata", getMetadata().toJSONString());
        portJSON.put("connectedEdges", getConnectedEdgesId());
        portJSON.put("type", getType());
    }
}
