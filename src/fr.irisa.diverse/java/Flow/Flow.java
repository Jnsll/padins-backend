package Flow;

import Core.Workspace;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/** A flow is the JSON file containing all the data structure of a workspace.
 * The web interface uses it, and only it, to create the view.
 *
 * Created by antoine on 26/05/2017.
 */
public class Flow {

    // Attributes
    JSONObject flow = null;
    Workspace owningWorkspace = null;
    // The below attributes have to be contained into the flow object.
    String id = "";
    String name = "";
    String description = "";
    String library = "";
    ArrayList<Edge> edges = null;
    ArrayList<Node> nodes = null;
    ArrayList<Group> groups = null;

    // Constructor
    public Flow (Workspace workspace) {
        this.id = UUID.randomUUID().toString();
        this.owningWorkspace = workspace;
        this.flow = new JSONObject();

        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
        this.groups = new ArrayList<Group>();
    }

    /** Constructor that creates a Flow from a JSONObject.
     * Use case : after server restart, re-create workspaces from the saved JSON files.
     *
     * @param source
     */
    public Flow (JSONObject source) {
        // TODO
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    public String serialize () {
        // Build the JSON file of the flow
        flow.put("id", id);
        flow.put("name", name);
        flow.put("library", library);
        flow.put("description", description);
        flow.put("edges", Utils.JSON.jsonArrayListToString(edges));
        flow.put("nodes", Utils.JSON.jsonArrayListToString(nodes));
        flow.put("groups", Utils.JSON.jsonArrayListToString(groups));

        // Return it as a JSON String to send it to frontend
        return flow.toJSONString();
    }

    public void addEdge (JSONObject src, JSONObject tgt, JSONObject metadata, String graph) {
        String srcNodeId = (String) src.get("node");
        String tgtNodeId = (String) src.get("node");

        if(nodeExist(srcNodeId) && nodeExist(tgtNodeId) && graphExist(graph)) {
            Edge newEdge = new Edge(src, tgt, metadata, graph);
            edges.add(newEdge);
        } else {
            System.err.println("[ERROR] Cannot create graph for src : " + srcNodeId + ", target : " + tgtNodeId + ", graph : " + graph + " because one of them doesn't exist");
        }
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private boolean nodeExist (String id) {
        // Go trough all the nodes and if it finds one with the given id return true, else return false
        for(int i = 0; i<nodes.size();i++){
            if(id.equals(this.nodes.get(i).getId())) return true;
        }

        return false;
    }

    private boolean edgeExist (String id) {
        // Go trough all the nodes and if it finds one with the given id return true, else return false
        for(int i = 0; i<edges.size();i++){
            if(id.equals(this.edges.get(i).getId())) return true;
        }

        return false;
    }

    private boolean graphExist (String id) {
        if (this.id.equals(id)) return true;

        for (int i=0; i<groups.size(); i++) {
            if(id.equals(groups.get(i).getId())) return true;
        }

        return false;
    }

}
