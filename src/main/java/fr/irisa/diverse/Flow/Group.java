package fr.irisa.diverse.Flow;

import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.Utils.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.UUID;

/** A group is a part of the flow, that has quite the same structure.
 *
 * This is a concept of the flow-based programming network protocol, as described here :
 * https://flowbased.github.io/fbp-protocol/#graph-addgroup
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

    /* =================================================================================================================
                                                    CONSTRUCTORS
       ===============================================================================================================*/


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

    /**
     * Get the unique id of the group
     * @return {String} the unique id of the group
     */
    public String getId (){
        if (id == null) id = "";

        return id;
    }

    /**
     * Get the name of the group. The name is user-defined.
     * @return {String} the name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Get the metadata of the group
     * @return {JSONObject} an object containing all the metadata of the group
     */
    public JSONObject getMetadata() {
        return metadata;
    }

    /**
     * Get the graph (the root flow or another group) into which this group is.
     * @return {String} the id of the graph
     */
    public String getGraph() {
        return graph;
    }

    /**
     * Get the list of nodes that are in this group
     * @return {JSONArray} the array containing the ids of the nodes
     */
    public JSONArray getNodes() {
        return nodes;
    }

    /**
     * Set the name of the group
     * @param name {string} the new name of the graph
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Update the metadata
     * @param metadata {JSONObject} a json containing all the metadata
     */
    public void setMetadata(JSONObject metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the group as a JSONObject. It will contain :
     * - id {String}
     * - name {String}
     * - metadata {JSONObject}
     * - graph {String}
     * - nodes {Array<String>}
     * @return {JSONObject} the object containing all the data of the group
     */
    public JSONObject getJson() {
        build();
        return group;
    }

    /**
     * Get the status of the group : running or not and a few more information. See class Status
     * @return {Status} the status of the group.
     */
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

    /**
     * Build the json object representing the group.
     * The json is used to send all the information of this group to any other service.
     *
     * The JSONObject contains :
     * - id {String}
     * - name {String}
     * - metadata {JSONObject}
     * - graph {String}
     * - nodes {Array<String>}
     */
    private void build () {
        // Build the group JSON
        group.put("id", getId());
        group.put("name", getName());
        group.put("metadata", getMetadata());
        group.put("graph", getGraph());
        group.put("nodes", getNodes());
    }
}
