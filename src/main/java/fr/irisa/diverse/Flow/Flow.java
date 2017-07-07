package fr.irisa.diverse.Flow;

import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.Utils.Status;
import fr.irisa.diverse.Utils.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

/**
 * The Flow is the main data structure of the project.
 *
 * It contains the graph, that is the ensemble of elements that describe the process that the user
 * wants to execute/simulate. This process is composed of nodes, connected with edges.
 *
 * The flow also contains groups, that are some subgraph of the graph. They are used to let the user
 * simulate some part of the graph instead of everything.
 *
 * Beside that, the flow contains the library of components available.
 *
 * We represent and store the flow as a JSON file.
 * The web interface uses it, and only it, to create the view.
 *
 * Created by antoine on 26/05/2017.
 */
public class Flow implements FlowInterface {

    // Attributes
    private JSONObject flow = null;
    public Workspace owningWorkspace = null;
    // The below attributes have to be contained into the flow object.
    private String id = "";
    private String description = "";
    private String componentsLibrary = "";
    private ArrayList<Edge> edges = null;
    private ArrayList<Node> nodes = null;
    private ArrayList<Group> groups = null;
    private Status status = null;

    /* =================================================================================================================
                                                CONSTRUCTORS
       ===============================================================================================================*/

    public Flow (Workspace workspace) {
        this.id = workspace.getUuid();
        this.owningWorkspace = workspace;
        this.flow = new JSONObject();

        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.groups = new ArrayList<>();

        componentsLibrary = owningWorkspace.getLibrary();

        this.status = new Status();
    }

    /** Constructor that creates a Flow from a JSONObject.
     * Use case : after server restart, re-create workspaces' flows from the saved JSON files.
     *
     * @param source : the parsed file
     */
    public Flow (JSONObject source, Workspace workspace) {
        this.flow = source;
        this.status = new Status();
        this.owningWorkspace = workspace;

        this.id = source.get("id") != null ? (String) source.get("id") : "";
        this.componentsLibrary = source.get("library") != null ? (String) source.get("library") : "";
        this.description = source.get("description") != null ? (String) source.get("description") : "";
        this.edges = new ArrayList<>();
        this.nodes = new ArrayList<>();
        this.groups = new ArrayList<>();

        JSONParser parser = new JSONParser();

        // Add nodes
        if (source.get("nodes") == null) { this.nodes = new ArrayList<>(); }
        else {
            ArrayList<JSONObject> nodes = (ArrayList) source.get("nodes");
            for (int i=0; i < nodes.size(); i++) {
                JSONObject a = nodes.get(i);
                // Add each node
                addNode((String) a.get("id"), (String) a.get("component"), (JSONObject) a.get("metadata"), (String) a.get("graph"),
                        ComponentsUtils.getComponent(componentsLibrary, (String) a.get("component")).isExecutable());
            }
        }

        // Add edges
        if (source.get("edges") == null) { this.edges = new ArrayList<>(); }
        else {
            ArrayList<JSONObject> edges = (ArrayList) source.get("edges");
            for (int i=0; i < edges.size(); i++) {
                JSONObject a = edges.get(i);

                JSONObject src = new JSONObject();
                JSONObject tgt = new JSONObject();
                JSONObject metadata = new JSONObject();
                try {
                    src = (JSONObject) parser.parse((String) a.get("src"));
                    tgt = (JSONObject) parser.parse((String) a.get("tgt"));
                    metadata = (JSONObject) parser.parse((String) a.get("metadata"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                addEdge((String) a.get("id"), src, tgt, metadata, (String) a.get("graph"));
            }
        }

        // Add groups
        if (source.get("groups") == null) { this.groups = new ArrayList<>(); }
        else {
            ArrayList<JSONObject> groups = (ArrayList) source.get("groups");
            for (int i=0; i < groups.size(); i++) {
                JSONObject a = groups.get(i);
                // addGroup(String name, JSONArray nodes, JSONObject metadata, String graph)
                addGroup((String) a.get("name"),(JSONArray) a.get("nodes"),(JSONObject) a.get("metadata"),
                        (String) a.get("graph"));
            }
        }

    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    /**
     * Serialize the Flow as a JSON and return it
     *
     * @return a JSON representation of the flow
     */
    public String serialize () {
        buildObject();
        // Return it as a JSON String to send it to frontend
        return flow.toJSONString();
    }

    /**
     * Build the Json object with the following structure
     *
     * {
     *     'id': string,
     *     'name': string,
     *     'library': string,
     *     'description': string,
     *     'edges': Edge[],
     *     'nodes': Node[],
     *     'groups': Group[]
     * }
     *
     */
    private void buildObject() {
        // Preliminary step : build JSONArray for edges, nodes and groups
        JSONArray edges = JSON.jsonArrayFromArrayList(this.edges);
        JSONArray nodes = JSON.jsonArrayFromArrayList(this.nodes);
        JSONArray groups = JSON.jsonArrayFromArrayList(this.groups);
        // Build the JSON file of the flow
        flow.put("id", id);
        flow.put("name", owningWorkspace.getName());
        flow.put("library", componentsLibrary);
        flow.put("description", description);
        flow.put("edges", edges);
        flow.put("nodes", nodes);
        flow.put("groups", groups);
    }

    /**
     * Add a new node onto the graph
     *
     * @param id The id of the node
     * @param component The component of the node
     * @param metadata The metadata object of the node
     * @param graph The graph in which to add the node
     * @param executable Whether the node is executable or not
     * @return True if added
     */
    public boolean addNode(String id, String component, JSONObject metadata, String graph, boolean executable) {
        if (graphExist(graph) && !nodeExist(id)){
            Node n = new Node(id, component, metadata, graph, executable, this);

            return nodes.add(n);
        }

        return false;
    }

    /**
     * Remove an existing node from the graph.
     *
     * @param id The id of the node to remove
     * @param graph The graph from which to remove the node
     * @return True if removed
     */
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

    /**
     * Change the id of a node
     *
     * @param from : the previous id
     * @param to : the new id
     * @param graph : the graph where the node is
     * @return True if successfully done
     */
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

    /**
     * Update the metadata of a node.
     *
     * @param id the id of the node
     * @param metadata the new metadata
     * @param graph the graph where the node is
     * @return True if successfully done
     */
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

    /**
     * Add an edge, connecting two nodes on the graph.
     *
     * @param id the id of the new edge
     * @param src the src node of the edge
     * @param tgt the tgt node of the edge
     * @param metadata the metadata of the edge
     * @param graph the graph where the edge is
     * @return True if added
     */
    public boolean addEdge (String id, JSONObject src, JSONObject tgt, JSONObject metadata, String graph) {
        String srcNodeId = (String) src.get("node");
        String tgtNodeId = (String) tgt.get("node");

        if(nodeExist(srcNodeId) && nodeExist(tgtNodeId) && graphExist(graph) && !edgeExist(src, tgt)) {
            Edge newEdge = new Edge(src, tgt, metadata, graph, id, this);
            edges.add(newEdge);

            Node srcNode = nodes.get(indexOfNode(srcNodeId));
            srcNode.assignPortToEdge((String) src.get("port"), newEdge.getId());

            Node tgtNode = nodes.get(indexOfNode(tgtNodeId));
            tgtNode.assignPortToEdge((String) tgt.get("port"), newEdge.getId());

            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove an existing edge from the graph.
     *
     * @param id the id of the edge
     * @param graph the graph where the edge is
     * @param src the src node of the edge
     * @param tgt the tgt node of the edge
     * @return True if removed
     */
    public boolean removeEdge(String id, String graph, JSONObject src, JSONObject tgt) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && edgeExist(src, tgt)) {
            // If so, retrieve the index of the edge and remove it
            edges.remove(indexOfEdge(src, tgt));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Modify the src, tgt and metadata of an edge
     *
     * @param id the unique id of the edge
     * @param graph the graph where the edge is
     * @param metadata the new metadata of the edge
     * @param src the new src object of the edge
     * @param tgt the tgt object of the edge
     * @return True if changed
     */
    public boolean changeEdge(String id, String graph, JSONObject metadata, JSONObject src, JSONObject tgt) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && edgeExist(id)) {
            // If so, retrieve the edge and modify its metadata
            Edge e = edges.get(indexOfEdge(id));
            e.setSrc(src);
            e.setTgt(tgt);
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

    /**
     * Create a new group that is a kind of subgraph user can run independently.
     *
     * @param name the name of the new group
     * @param nodes the nodes in this group
     * @param metadata the metadata of the new group
     * @param graph the graph where the group is (can be another group)
     * @return
     */
    public boolean addGroup(String name, JSONArray nodes, JSONObject metadata, String graph) {
        if(graphExist(graph) && !groupExist(name)){
            Group g = new Group(name, nodes, metadata, graph, this);

            groups.add(g);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove an existing group
     *
     * @param name name of the group to delete
     * @param graph the graph where the group is
     * @return
     */
    public boolean removeGroup(String name, String graph) {
        if(graphExist(graph) && groupExist(name)) {
            groups.remove(indexOfGroup(name));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Rename a group.
     *
     * @param from the old name
     * @param to the new name
     * @param graph the graph where the group is
     * @return
     */
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

    /**
     * Change the metadata of a group.
     *
     * @param name name of the group
     * @param metadata new metadata of the group
     * @param graph the graph where the group is
     * @return
     */
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

    /**
     * Determine which nodes are the first one on the flow and give the list of them.
     *
     * @param nodes The nodes composing the flow
     * @return The list of first nodes to execute in order to run the flow.
     */
    public ArrayList<Node> findFirstNodesOfFlow (ArrayList<Node> nodes) {
        ArrayList<Node> res = new ArrayList<>();
        // First, in case nodes is composed of only one node, we return the node
        if (nodes.size() == 1 ) return nodes;
        // Elsewhere, we search for the node in the list that doesn't have a previous node and that have a next one.
        for (Node n : nodes) {
            if(n.previousInFlow() == null && n.nextInFlow() != null) res.add(n);
        }

        // Finally return the list
        return res;
    }

    /* =================================================================================================================
                                                    GETTERS AND SETTERS
       ===============================================================================================================*/

    /**
     * The components library is the library that contains all the components the user will be able to use
     * in order to build his flow.
     *
     * @return The name of the library
     */
    public String getComponentsLibrary() {
        return componentsLibrary;
    }

    /**
     * The unique id of the Flow
     * @return The unique id of the Flow as String
     */
    public String getId() {
        return id;
    }

    /**
     * @return the flow as JSONObject
     */
    public JSONObject getFlowObject () {
        if (this.flow == null) {
            this.flow = new JSONObject();
        }
        buildObject();
        return this.flow;
    }


    /**
     * Get an edge from its source and target nodes
     *
     * @param src the source node of the edge. Src format is : {node: string(id), port: string}
     * @param tgt the target node of the edge. Tgt format is : {node: string(id), port: string}
     * @param graph the graph where the edge is supposed to be
     * @return the Edge if found, null if not
     */
    public Edge getEdge (JSONObject src, JSONObject tgt, String graph) {
        if (graphExist(graph) && edgeExist(src, tgt)) {
            return edges.get(indexOfEdge(src, tgt));
        } else {
            return null;
        }
    }

    /**
     * Get an edge from its id
     * @param id the id of the edge
     * @return the Edge if found, null if not
     */
    public Edge getEdge (String id) {
        // Look at each edge and if its id is the same as the given one, returns it.
        for (int i=0; i<edges.size(); i++) {
            if (edges.get(i).getId().equals(id)) return edges.get(i);
        }

        return null;
    }

    /**
     * @return the list of nodes
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * Get the list of nodes of a given group.
     *
     * @param g the group
     * @return the list of nodes that are in the given group
     */
    public ArrayList<Node> getNodes (Group g) {
        JSONArray nodesId = g.getNodes();
        ArrayList<Node> res = new ArrayList<>();

        for (Object o : nodesId) {
            String id = (String) o;
            res.add(getNode(id, this.id));
        }

        return res;
    }

    /**
     * Get a node
     *
     * @param id the id of the node
     * @param graph the graph where the node is
     * @return the Node if found, null if not
     */
    public Node getNode (String id, String graph) {
        if (graphExist(graph) && nodeExist(id)) {
            return nodes.get(indexOfNode(id));
        } else {
            return null;
        }
    }

    /**
     * Get a group
     *
     * @param name the name of the group
     * @param graph the graph where the Group is
     * @return the Group if found, null if not
     */
    public Group getGroup (String name, String graph) {
        if (graphExist(graph) && groupExist(name)) {
            return groups.get(indexOfGroup(name));
        } else {
            return null;
        }
    }

    /**
     * Get a graph
     *
     * @param graph the id of the graph
     * @return the graph as an object that can be Flow or Group
     */
    public Object getGraph (String graph) {
        if (graph.equals(id)) return this;
        else {
            // It means that graph is a group
            return getGroup(graph, id);
        }
    }

    /**
     * Set the description of the flow/project.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the status of the Flow
     * @return the Status instance
     */
    public Status getStatus() {
        return status;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    /**
     * Test whether a node exists or not
     *
     * @param id the id of the node
     * @return True if the node exists
     */
    private boolean nodeExist (String id) {
        // Go trough all the nodes and if it finds one with the given id return true, else return false
        for (Node node : nodes) {
            if (id.equals(node.getId())) return true;
        }

        return false;
    }

    /**
     * Test whether an edge exists or not
     *
     * @param src the source node of the edge. Src format is : {node: string(id), port: string}
     * @param tgt the target node of the edge. Tgt format is : {node: string(id), port: string}
     * @return True if exists
     */
    private boolean edgeExist (JSONObject src, JSONObject tgt) {
        return indexOfEdge(src, tgt) != -1;
    }

    /**
     * Test whether an edge exists or not
     *
     * @param id the id of the edge
     * @return True if exists
     */
    private boolean edgeExist(String id) {
        return indexOfEdge(id) != -1;
    }

    /**
     * Test whether a graph exists or not
     *
     * @param id the id of the graph
     * @return True if exists
     */
    private boolean graphExist (String id) {
        if (this.id.equals(id)) return true;

        for (Group group : groups) {
            if (id.equals(group.getId())) return true;
        }

        return false;
    }

    /**
     * Test whether a group exists or not
     *
     * @param name the name of the group
     * @return True if exists
     */
    private boolean groupExist (String name) {
        if (this.groups == null) return false;

        for (Group group : groups) {
            if (group.getName().equals(name)) return true;
        }

        return false;
    }

    /**
     * Get the index of an edge in the edges array
     *
     * @param src the source node of the edge. Src format is : {node: string(id), port: string}
     * @param tgt the target node of the edge. Tgt format is : {node: string(id), port: string}
     * @return the index of the edge, -1 if not in edges
     */
    private int indexOfEdge (JSONObject src, JSONObject tgt) {
        for(int i=0; i<edges.size(); i++) {
            if (edges.get(i).getSrc().equals(src) && edges.get(i).getTgt().equals(tgt)) return i;
        }

        return -1;
    }

    /**
     * Get the index of an edge in the edges array
     *
     * @param id the id of the edge
     * @return the index of the edge, -1 if not in edges
     */
    private int indexOfEdge (String id) {
        for(int i=0; i<edges.size(); i++) {
            if (edges.get(i).getId().equals(id)) return i;
        }

        return -1;
    }

    /**
     * Get the index of a node in the nodes array
     *
     * @param id the id of the node
     * @return the index of the node, -1 if not in nodes
     */
    private int indexOfNode (String id) {
        for(int i=0; i<nodes.size(); i++) {
            if (nodes.get(i).getId().equals(id)) return i;
        }

        return -1;
    }

    /**
     * Get the index of a group in the groups array
     *
     * @param name the name of the group
     * @return the index of the group, -1 if not in groups
     */
    private int indexOfGroup (String name) {
        for(int i=0; i<groups.size(); i++) {
            if (groups.get(i).getName().equals(name)) return i;
        }

        return -1;
    }

}
