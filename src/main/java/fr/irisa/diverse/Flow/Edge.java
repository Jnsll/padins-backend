package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

import java.util.UUID;

/**
 * Visually, an edge is a link between two nodes.
 * An edge is a part of the flow.
 * The notion is described in the Flow-based programming paradigm.
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

    /* =================================================================================================================
                                                   CONSTRUCTORS
       ===============================================================================================================*/

    public Edge(JSONObject src, JSONObject tgt, JSONObject metadata, String graph, Flow owningFlow) {
        this.owningFlow = owningFlow;
        this.edge = new JSONObject();
        this.id = UUID.randomUUID().toString();
        this.src = src;
        this.tgt = tgt;
        this.metadata = metadata;
        this.graph = graph;
    }

    public Edge(JSONObject src, JSONObject tgt, JSONObject metadata, String graph, String id, Flow owningFlow) {
        this(src, tgt, metadata, graph, owningFlow);

        this.id = id;
    }

    /* =================================================================================================================
                                             GETTERS AND SETTERS FUNCTIONS
       ===============================================================================================================*/

    /**
     * Give the unique id of the edge
     *
     * @return its uuid
     */
    public String getId (){
        if (id == null) id = "";

        return id;
    }

    /**
     * Give the source element of the edge.
     *
     * As described in the FBP Network Protocol, the source element is composed of :
     * - The node id
     * - The port of the node the edge is connected to
     *
     * @return the src el as JSONObject
     */
    public JSONObject getSrc () {
        if (src == null) src = new JSONObject();

        return src;
    }

    /**
     * Set the source element of the edge.
     *
     * As described in the FBP Network Protocol, the source element is composed of :
     * - The node id
     * - The port of the node the edge is connected to
     *
     * @param src the FBP compliant src element
     */
    public void setSrc (JSONObject src) {
        this.src = src;
    }

    public JSONObject getTgt () {
        if (tgt == null) tgt = new JSONObject();

        return tgt;
    }

    /**
     * Set the target element of the edge.
     *
     * As described in the FBP Network Protocol, the target element is composed of :
     * - The node id
     * - The port of the node the edge is connected to
     *
     * @param tgt the FBP compliant tgt element
     */
    public void setTgt (JSONObject tgt) {
        this.tgt = tgt;
    }

    /**
     * Give the metadata of the edhe
     *
     * @return the metadata as a JSONObject
     */
    public JSONObject getMetadata () {
        if (metadata == null) metadata = new JSONObject();

        return metadata;
    }

    /**
     * Replace the metadata of the edge with the given one
     *
     * @param metadata the new metadata
     */
    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    /**
     * Give the graph id on which the edge is.
     *
     * @return the unique id of the graph.
     */
    public String getGraph () {
        if (graph == null) graph = "";

        return graph;
    }

    /**
     * Give the JSONObject representing a node. The JSON has the following structure :
     * {
     *     'id': string,
     *     'src': object,
     *     'tgt': object,
     *     'metadata': object,
     *     'graph': string
     * }
     *
     * @return a JSONObject containing the edge's data
     */
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

    /**
     * Build the JSONObject of the edge.
     *
     * The JSON has the following structure :
     * {
     *     'id': string,
     *     'src': object,
     *     'tgt': object,
     *     'metadata': object,
     *     'graph': string
     * }
     */
    private void build () {
        // Build the edge JSON
        edge.put("id", getId());
        edge.put("src", getSrc().toJSONString());
        edge.put("tgt", getTgt().toJSONString());
        edge.put("metadata", getMetadata().toJSONString());
        edge.put("graph", getGraph());
    }
}
