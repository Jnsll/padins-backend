package Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/** A group is a part of the flow, that has quite the same structure.
 *
 * Created by antoine on 29/05/17.
 */
public class Group {

    // Attributes
    JSONObject group = null;
    Flow owningFlow = null;
    String id = "";
    String name = "";
    ArrayList<String> nodes = null;
    JSONObject metadata = null;
    String graph = "";

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

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        // Build the edge JSON
        group.put("id", getId());
        group.put("name", getName());
        group.put("metadata", getMetadata().toJSONString());
        group.put("graph", getGraph());
        group.put("nodes", nodesToString());

        // Return it as a String
        return group.toJSONString();
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/


    private ArrayList<String> extractNodesFromJSON (JSONObject nodes) {
        ArrayList<String> res = new ArrayList<>();

        // Go through the JSON object named nodes to retrieve each node's unique id
        Set keys = nodes.keySet();
        Iterator iterator = keys.iterator();
        while(iterator.hasNext()) {
            res.add((String) nodes.get(iterator.next()));
        }

        return res;
    }

    private String nodesToString () {
        return Utils.JSON.jsonArrayListToString(nodes);
    }
}
