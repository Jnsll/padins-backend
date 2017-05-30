package Flow;

import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * Created by antoine on 29/05/17.
 */
class Port {

    // Attributes
    private JSONObject portJSON = null;
    private String id = "";
    private String port = "";
    private JSONObject metadata = null;
    private String connectedTo = "";
    private final String type = "object";

    // Constructors
    public Port (String port) {
        id = UUID.randomUUID().toString();
        this.portJSON = new JSONObject();
        this.port = port;
        this.metadata = new JSONObject();
    }

    public Port (String port, JSONObject metadata) {
        this(port);
        this.metadata = metadata;
    }

    public Port (String port, JSONObject metadata, String connectedTo) {
        this(port, metadata);

        this.connectedTo = connectedTo;
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

    public String getConnectedTo () {
        if (connectedTo == null) connectedTo = "";

        return connectedTo;
    }

    public String getType() {
        return type;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        // Build the edge JSON
        portJSON.put("id", getId());
        portJSON.put("port", getPort());
        portJSON.put("metadata", getMetadata().toJSONString());
        portJSON.put("connectedTo", getConnectedTo());
        portJSON.put("type", getType());

        // Return it as a String
        return portJSON.toJSONString();
    }
}
