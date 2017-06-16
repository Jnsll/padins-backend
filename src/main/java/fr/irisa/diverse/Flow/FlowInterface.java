package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by antoine on 30/05/17.
 */
interface FlowInterface {

    // Methods used to modify the graph

    /**
     * Add a node to the flow
     *
     * @param id The id of the node
     * @param component The component of the node
     * @param metadata The metadata object of the node
     * @param graph The graph in which to add the node
     * @param executable Whether the node is executable or not
     * @return True if the node has been added
     */
    boolean addNode (String id, String component, JSONObject metadata, String graph, boolean executable);

    /**
     * Remove a node from the flow
     *
     * @param id The id of the node to remove
     * @param graph The graph from which to remove the node
     * @return
     */
    boolean removeNode (String id, String graph);

    boolean renameNode (String from, String to, String graph);

    boolean changeNode (String id, JSONObject metadata, String graph);

    boolean addEdge (String id, JSONObject src, JSONObject tgt, JSONObject metadata, String graph);

    boolean removeEdge (String graph, JSONObject src, JSONObject tgt);

    boolean changeEdge (String graph, JSONObject metadata, JSONObject src, JSONObject tgt);

    boolean addInitial (String graph, JSONObject metadata, JSONObject src, JSONObject tgt);

    boolean removeInitial (String graph, JSONObject src, JSONObject tgt);

    boolean addInport (String name, String node, String port, JSONObject metadata, String graph);

    boolean removeInport (String name, String graph);

    boolean renameInport (String from, String to, String graph);

    boolean addOutport (String name, String node, String port, JSONObject metadata, String graph);

    boolean removeOutport (String name, String graph);

    boolean renameOutport (String from, String to, String graph);

    boolean addGroup (String name, JSONArray nodes, JSONObject metadata, String graph);

    boolean removeGroup (String name, String graph);

    boolean renameGroup (String from, String to, String graph);

    boolean changeGroup (String name, JSONObject metadata, String graph);

    // Methods used to read information

    String serialize ();


}
