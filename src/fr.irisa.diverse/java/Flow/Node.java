package Flow;

import org.json.simple.JSONObject;
import java.util.UUID;

/**
 * A node correspond to a block on a flow-based program
 * A node is a part of a flow.
 *
 * Created by antoine on 29/05/17.
 */
public class Node {

    // Attributes
    JSONObject node = null;
    String id = "";
    String component = "";
    JSONObject metadata = null;
    String graph = null;
    Ports inports = null;
    Ports outports = null;

    // Constructor
    public Node (String component, JSONObject metadata, String graph) {
        node = new JSONObject();
        id = UUID.randomUUID().toString();
        this.component = component;
        this.metadata = metadata;
        this.graph = graph;
        this.inports = ComponentsUtils.getInPortsForComponent(component);
        this.outports = ComponentsUtils.getOutPortsForComponent(component);
    }

    /* =================================================================================================================
                                             GETTERS AND SETTERS FUNCTIONS
       ===============================================================================================================*/

    public String getId (){
        if (id == null) id = "";

        return id;
    }

    public Ports getInports() {
        return inports;
    }

    public Ports getOutports() {
        return outports;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public String getGraph() {
        return graph;
    }

    public String getComponent() {
        return component;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        // Build the edge JSON
        node.put("id", getId());
        node.put("component", getComponent());
        node.put("metadata", getMetadata().toJSONString());
        node.put("graph", getGraph());
        node.put("inports", getInports().toString());
        node.put("outports", getOutports().toString());

        // Return it as a String
        return node.toJSONString();
    }
}
