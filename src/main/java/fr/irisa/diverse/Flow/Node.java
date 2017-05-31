package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
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
    private JSONObject node = null;
    private Flow owningFlow = null;
    private String id = "";
    private String component = "";
    private JSONObject metadata = null;
    private String graph = null;
    private Ports inports = null;
    private Ports outports = null;

    // Constructor
    public Node (String id, String component, JSONObject metadata, String graph, Flow owningFlow) {
        node = new JSONObject();
        this.owningFlow = owningFlow;
        this.component = component;
        this.metadata = metadata;
        this.graph = graph;
        this.inports = ComponentsUtils.getInPortsForComponent(owningFlow.getComponentsLibrary(), component, id);
        this.outports = ComponentsUtils.getOutPortsForComponent(owningFlow.getComponentsLibrary(), component, id);
        this.id = id;
    }

    /* =================================================================================================================
                                             GETTERS AND SETTERS FUNCTIONS
       ===============================================================================================================*/

    public String getId (){
        if (id == null) id = "";

        return id;
    }

    public void setId (String newId) { id = newId; }

    public Ports getInports() {
        return inports;
    }

    public Ports getOutports() {
        return outports;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JSONObject metadata) { this.metadata = metadata; }

    public String getGraph() {
        return graph;
    }

    public String getComponent() {
        return component;
    }

    public JSONObject getJson() {
        build();
        return node;
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    @Override
    public String toString () {
        build();
        // Return it as a String
        return node.toJSONString();
    }

    public void assignPortToEdge (String port, String edge) {
        Port p = findPort(port);
        if (p != null) {
            p.setConnectedEdge(edge);
        }
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private void build () {
        // Build the node JSON
        node.put("id", getId());
        node.put("component", getComponent());
        node.put("metadata", getMetadata());
        node.put("graph", getGraph());
        node.put("inports", getInports().toJson());
        node.put("outports", getOutports().toJson());
    }

    private Port findPort (String name) {
        // First search into inports
        Port res = findPortInGivenObject(inports, name);
        // If not found into inports, search into outports
        if (res == null) res = findPortInGivenObject(outports, name);

        // Return the result
        return res;
    }

    private Port findPortInGivenObject (Ports ports, String name) {
        for(Object o : ports) {
            if (o instanceof  Port && ((Port) o).getName().equals(name)) {
                return (Port) o;
            }
        }

        return null;
    }
}
