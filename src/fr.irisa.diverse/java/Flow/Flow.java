package Flow;

import Core.Workspace;
import org.json.simple.JSONObject;

import java.util.ArrayList;

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
        this.owningWorkspace = workspace;
        this.flow = new JSONObject();

        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
        this.groups = new ArrayList<Group>();
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    public void addEdge (JSONObject src, JSONObject tgt, JSONObject metadata, String graph) {
        String srcNodeId = (String) src.get("node");
        String tgtNodeId = (String) src.get("node");

        if(nodeExist(srcNodeId) && nodeExist(tgtNodeId) && graphExist(graph)) {
            Edge newEdge = new Edge(src, tgt, metadata, graph);
        } else {
            System.err.println("[ERROR] Cannot create graph for src : " + srcNodeId + ", target : " + tgtNodeId + ", graph : " + graph + " because one of them doesn't exist");
        }
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private boolean nodeExist (String id){
        // Go trough all the nodes and if it finds one with the given id return true, else return false
        for(int i = 0; i<nodes.size();i++){
            if(id.equals(this.nodes.get(i).getId())) return true;
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
