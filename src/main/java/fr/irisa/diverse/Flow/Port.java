package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

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
    private String connectedEdge = "";
    private final String type = "object";

    // Constructors
    Port (String port, String name) {
        id = UUID.randomUUID().toString();
        this.portJSON = new JSONObject();
        this.port = port;
        this.name = name;
        this.metadata = new JSONObject();
    }

    Port (String port, String name, JSONObject metadata) {
        this(port, name);
        this.metadata = metadata;
    }

    Port (String port, String name, JSONObject metadata, String connectedEdge) {
        this(port, name, metadata);

        this.connectedEdge = connectedEdge;
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

    public String getConnectedEdgeId () {
        if (connectedEdge == null) connectedEdge = "";

        return connectedEdge;
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
        portJSON.put("connectedEdge", getConnectedEdgeId());
        portJSON.put("type", getType());
    }
}
