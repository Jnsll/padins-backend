package fr.irisa.diverse.Flow;

import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.Utils.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

/** A group is a part of the flow, that has quite the same structure.
 *
 * Created by antoine on 29/05/17.
 */
public class Group {

    // Attributes
    private JSONObject group = null;
    private Flow owningFlow = null;
    private String id = "";
    private String name = "";
    private JSONArray nodes = null;
    private JSONObject metadata = null;
    private String graph = "";
    private Status status = null;

    // Constructor
    public Group (String name, JSONArray nodes, JSONObject metadata, String graph, Flow owningFlow) {
        group = new JSONObject();
        this.owningFlow = owningFlow;
        id = UUID.randomUUID().toString();
        this.name = name;
        this.nodes = nodes;
        this.metadata = metadata;
        this.graph = graph;

    }

    public Group (String name, JSONArray nodes, JSONObject metadata, String graph, String id, Flow owningFlow) {
        this(name, nodes, metadata, graph, owningFlow);

        this.id = id;
    }

    /* =================================================================================================================
                                                    GETERS AND SETTERS
       ===============================================================================================================*/

    public String getId (){
        if (id == null) id = "";

        return id;
    }

    public String getName() {
        return name;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public String getGraph() {
        return graph;
    }

    public JSONArray getNodes() {
        return nodes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    public JSONObject getJson() {
        build();
        return group;
    }

    public Status getStatus() {
        return status;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        build();

        // Return it as a String
        return group.toJSONString();
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private void build () {
        // Build the group JSON
        group.put("id", getId());
        group.put("name", getName());
        group.put("metadata", getMetadata());
        group.put("graph", getGraph());
        group.put("nodes", getNodes());
    }
}
