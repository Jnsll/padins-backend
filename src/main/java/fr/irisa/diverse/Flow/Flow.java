package fr.irisa.diverse.Flow;

import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.Utils.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/** A flow is the JSON file containing all the data structure of a workspace.
 * The web interface uses it, and only it, to create the view.
 *
 * Created by antoine on 26/05/2017.
 */
public class Flow implements FlowInterface {

    // Attributes
    private JSONObject flow = null;
    private Workspace owningWorkspace = null;
    // The below attributes have to be contained into the flow object.
    private String id = "";
    private String name = "";
    private String description = "";
    private String componentsLibrary = "";
    private ArrayList<Edge> edges = null;
    private ArrayList<Node> nodes = null;
    private ArrayList<Group> groups = null;

    // Constructor
    public Flow (Workspace workspace) {
        this.id = workspace.getUuid();
        this.owningWorkspace = workspace;
        this.flow = new JSONObject();

        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
        this.groups = new ArrayList<Group>();

        componentsLibrary = owningWorkspace.getLibrary();
    }

    /** Constructor that creates a fr.irisa.diverse.Flow from a JSONObject.
     * Use case : after server restart, re-create workspaces from the saved JSON files.
     *
     * @param source : the parsed file
     */
    public Flow (JSONObject source) {
        // TODO
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    public String serialize () {
        // Preliminary step : build JSONArray for edges, nodes and groups
        JSONArray edges = JSON.jsonArrayFromArrayList(this.edges);
        JSONArray nodes = JSON.jsonArrayFromArrayList(this.nodes);
        JSONArray groups = JSON.jsonArrayFromArrayList(this.groups);
        // Build the JSON file of the flow
        flow.put("id", id);
        flow.put("name", name);
        flow.put("library", componentsLibrary);
        flow.put("description", description);
        flow.put("edges", edges);
        flow.put("nodes", nodes);
        flow.put("groups", groups);

        // Return it as a JSON String to send it to frontend
        return flow.toJSONString();
    }

    public boolean addNode(String id, String component, JSONObject metadata, String graph) {
        Node n = new Node(id, component, metadata, graph, this);

        return nodes.add(n);
    }

    public boolean removeNode(String id, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && nodeExist(id)) {
            // If so, retrieve the index of the node and remove it
            nodes.remove(indexOfNode(id));
            return true;
        } else {
             return false;
        }
    }

    public boolean renameNode(String from, String to, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && nodeExist(from)) {
            // If so, retrieve the node and modify its id
            Node n = nodes.get(indexOfNode(from));
            n.setId(to);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeNode(String id, JSONObject metadata, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && nodeExist(id)) {
            // If so, retrieve the node and modify its id
            Node n = nodes.get(indexOfNode(id));
            n.setMetadata(metadata);
            return true;
        } else {
            return false;
        }
    }

    public boolean addEdge (JSONObject src, JSONObject tgt, JSONObject metadata, String graph) {
        String srcNodeId = (String) src.get("node");
        String tgtNodeId = (String) src.get("node");

        if(nodeExist(srcNodeId) && nodeExist(tgtNodeId) && graphExist(graph)) {
            Edge newEdge = new Edge(src, tgt, metadata, graph, this);
            edges.add(newEdge);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeEdge(String graph, JSONObject src, JSONObject tgt) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && edgeExist(src, tgt)) {
            // If so, retrieve the index of the edge and remove it
            edges.remove(indexOfEdge(src, tgt));
            return true;
        } else {
            return false;
        }
    }

    public boolean changeEdge(String graph, JSONObject metadata, JSONObject src, JSONObject tgt) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && edgeExist(src, tgt)) {
            // If so, retrieve the edge and modify its metadata
            Edge e = edges.get(indexOfEdge(src, tgt));
            e.setMetadata(metadata);
            return true;
        } else {
            return false;
        }
    }

    public boolean addInitial(String graph, JSONObject metadata, JSONObject src, JSONObject tgt) {
        // Not used for now
        return true;
    }

    public boolean removeInitial(String graph, JSONObject src, JSONObject tgt) {
        // Not used for now
        return true;
    }

    public boolean addInport(String name, String node, String port, JSONObject metadata, String graph) {
        // Not used for now
        return true;
    }

    public boolean removeInport(String name, String graph) {
        // Not used for now
        return true;
    }

    public boolean renameInport(String from, String to, String graph) {
        // Not used for now
        return true;
    }

    public boolean addOutport(String name, String node, String port, JSONObject metadata, String graph) {
        // Not used for now
        return true;
    }

    public boolean removeOutport(String name, String graph) {
        // Not used for now
        return true;
    }

    public boolean renameOutport(String from, String to, String graph) {
        // Not used for now
        return true;
    }

    public boolean addGroup(String name, JSONObject nodes, JSONObject metadata, String graph) {
        if(graphExist(graph)){
            Group g = new Group(name, nodes, metadata, graph, this);

            groups.add(g);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeGroup(String name, String graph) {
        if(graphExist(graph) && groupExist(name)) {
            groups.remove(indexOfGroup(name));
            return true;
        } else {
            return false;
        }
    }

    public boolean renameGroup(String from, String to, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && groupExist(from)) {
            // If so, retrieve the group and modify its name
            Group g = groups.get(indexOfGroup(from));
            g.setName(to);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeGroup(String name, JSONObject metadata, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && groupExist(name)) {
            // If so, retrieve the group and modify its metadata
            Group g = groups.get(indexOfGroup(name));
            g.setMetadata(metadata);
            return true;
        } else {
            return false;
        }
    }

    /* =================================================================================================================
                                                    GETTERS AND SETTERS
       ===============================================================================================================*/

    public String getComponentsLibrary() {
        return componentsLibrary;
    }

    public String getId() {
        return id;
    }

    public Edge getEdge (JSONObject src, JSONObject tgt, String graph) {
        if (graphExist(graph) && edgeExist(src, tgt)) {
            return edges.get(indexOfEdge(src, tgt));
        } else {
            return null;
        }
    }

    public Node getNode (String id, String graph) {
        if (graphExist(graph) && nodeExist(id)) {
            return nodes.get(indexOfNode(id));
        } else {
            return null;
        }
    }

    public Group getGroup (String name, String graph) {
        if (graphExist(graph) && groupExist(name)) {
            return groups.get(indexOfGroup(name));
        } else {
            return null;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private boolean nodeExist (String id) {
        // Go trough all the nodes and if it finds one with the given id return true, else return false
        for (Node node : nodes) {
            if (id.equals(node.getId())) return true;
        }

        return false;
    }

    private boolean edgeExist (JSONObject src, JSONObject tgt) {
        return indexOfEdge(src, tgt) != -1;
    }

    private boolean graphExist (String id) {
        if (this.id.equals(id)) return true;

        for (Group group : groups) {
            if (id.equals(group.getId())) return true;
        }

        return false;
    }

    private boolean groupExist (String name) {
        if (this.groups == null) return false;

        for (Group group : groups) {
            if (group.getName().equals(name)) return true;
        }

        return false;
    }

    private int indexOfEdge (JSONObject src, JSONObject tgt) {
        for(int i=0; i<edges.size(); i++) {
            if (edges.get(i).getSrc().equals(src) && edges.get(i).getTgt().equals(tgt)) return i;
        }

        return -1;
    }

    private int indexOfNode (String id) {
        for(int i=0; i<nodes.size(); i++) {
            if (nodes.get(i).getId().equals(id)) return i;
        }

        return -1;
    }

    private int indexOfGroup (String name) {
        for(int i=0; i<groups.size(); i++) {
            if (groups.get(i).getName().equals(name)) return i;
        }

        return -1;
    }

}
