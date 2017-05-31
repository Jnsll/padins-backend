package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

import java.util.UUID;

/** An edge is a link between two nodes.
 * An edge is a part of the flow.
 *
 * Created by antoine on 29/05/17.
 */
public class Edge {

    // Attributes
    private JSONObject edge = null;
    private Flow owningFlow = null;
    private String id = "";
    private JSONObject src = null;
    private JSONObject tgt = null;
    private JSONObject metadata = null;
    private String graph = "";

    // Constructor
    public Edge(JSONObject src, JSONObject tgt, JSONObject metadata, String graph, Flow owningFlow) {
        this.owningFlow = owningFlow;
        this.edge = new JSONObject();
        this.id = UUID.randomUUID().toString();
        this.src = src;
        this.tgt = tgt;
        this.metadata = metadata;
        this.graph = graph;
    }

    /* =================================================================================================================
                                             GETTERS AND SETTERS FUNCTIONS
       ===============================================================================================================*/

    public String getId (){
        if (id == null) id = "";

        return id;
    }

    public JSONObject getSrc () {
        if (src == null) src = new JSONObject();

        return src;
    }

    public JSONObject getTgt () {
        if (tgt == null) tgt = new JSONObject();

        return tgt;
    }

    public JSONObject getMetadata () {
        if (metadata == null) metadata = new JSONObject();

        return metadata;
    }

    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    public String getGraph () {
        if (graph == null) graph = "";

        return graph;
    }

    public JSONObject getJson() {
        build();
        return edge;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        build();
        // Return it as a String
        return edge.toJSONString();
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private void build () {
        // Build the edge JSON
        edge.put("id", getId());
        edge.put("src", getSrc().toJSONString());
        edge.put("tgt", getTgt().toJSONString());
        edge.put("metadata", getMetadata().toJSONString());
        edge.put("graph", getGraph());
    }
}
