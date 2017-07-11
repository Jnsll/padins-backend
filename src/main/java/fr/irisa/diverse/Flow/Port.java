package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
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

    // Constructors
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

    public String getId() {
        return id;
    }

    public String getPort () {
        if (port == null) port = "";

        return port;
    }

    public JSONObject getMetadata () {
        if (metadata == null) metadata = new JSONObject();

        return metadata;
    }

    public ArrayList<String> getConnectedEdgesId() {
        if (connectedEdges == null) connectedEdges = new ArrayList<>();

        return connectedEdges;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public void setConnectedEdges(ArrayList<String> connectedEdges) {
        this.connectedEdges = connectedEdges;
    }

    public void addConnectedEdge (String edgeId) {
        if (this.connectedEdges.indexOf(edgeId) == -1) {
            this.connectedEdges.add(edgeId);
        }
    }

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
