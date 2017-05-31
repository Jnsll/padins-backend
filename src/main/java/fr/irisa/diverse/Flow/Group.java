package fr.irisa.diverse.Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Set;
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
    private ArrayList<String> nodes = null;
    private JSONObject metadata = null;
    private String graph = "";

    // Constructor
    public Group (String name, JSONObject nodes, JSONObject metadata, String graph, Flow owningFlow) {
        group = new JSONObject();
        this.owningFlow = owningFlow;
        id = UUID.randomUUID().toString();
        this.name = name;
        this.nodes = extractNodesFromJSON(nodes);
        this.metadata = metadata;
        this.graph = graph;

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

    public ArrayList<String> getNodes() {
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
        group.put("metadata", getMetadata().toJSONString());
        group.put("graph", getGraph());
        group.put("nodes", nodesToString());
    }


    private ArrayList<String> extractNodesFromJSON (JSONObject nodes) {
        ArrayList<String> res = new ArrayList<>();

        // Go through the JSON object named nodes to retrieve each node's unique id
        Set keys = nodes.keySet();
        for (Object key : keys) {
            res.add((String) nodes.get(key));
        }

        return res;
    }

    private String nodesToString () {
        return fr.irisa.diverse.Utils.JSON.jsonArrayListToString(nodes);
    }
}
