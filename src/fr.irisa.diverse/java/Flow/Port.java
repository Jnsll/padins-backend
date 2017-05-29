package Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by antoine on 29/05/17.
 */
public class Port {

    // Attributes
    JSONObject portJSON = null;
    String port = "";
    JSONObject metadata = null;
    String connectedTo = "";

    // Constructors
    public Port (String port) {
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

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        // Build the edge JSON
        portJSON.put("port", getPort());
        portJSON.put("metadata", getMetadata().toJSONString());
        portJSON.put("connectedTo", getConnectedTo());

        // Return it as a String
        return portJSON.toJSONString();
    }
}
