package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Interface describing the methods a Flow must have.
 *
 * Most of the methods corresponds to messages exchanged on the Flow Based Programming Network Protocol.
 *
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

    /**
     * Change the id of a node
     *
     * @param from : the previous id
     * @param to : the new id
     * @param graph : the graph where the node is
     * @return True if successfully done
     */
    boolean renameNode (String from, String to, String graph);

    /**
     * Update the metadata of a node.
     *
     * @param id the id of the node
     * @param metadata the new metadata
     * @param graph the graph where the node is
     * @return True if successfully done
     */
    boolean changeNode (String id, JSONObject metadata, String graph);

    /**
     * Add an edge, connecting two nodes on the graph.
     *
     * @param id the id of the new edge
     * @param src the src node of the edge
     * @param tgt the tgt node of the edge
     * @param metadata the metadata of the edge
     * @param graph the graph where the edge is
     * @return True if successfully added and connected.
     */
    boolean addEdge (String id, JSONObject src, JSONObject tgt, JSONObject metadata, String graph);

    /**
     * Remove an existing edge from the graph.
     *
     * @param id the id of the edge
     * @param graph the graph where the edge is
     * @param src the src node of the edge
     * @param tgt the tgt node of the edge
     * @return True if successfully removed
     */
    boolean removeEdge (String id, String graph, JSONObject src, JSONObject tgt);

    /**
     * Modify the src, tgt and metadata of an edge
     *
     * @param id the unique id of the edge
     * @param graph the graph where the edge is
     * @param metadata the new metadata of the edge
     * @param src the new src object of the edge
     * @param tgt the tgt object of the edge
     * @return True if the modification has successfully be done
     */
    boolean changeEdge (String id, String graph, JSONObject metadata, JSONObject src, JSONObject tgt);

    boolean addInitial (String graph, JSONObject metadata, JSONObject src, JSONObject tgt);

    boolean removeInitial (String graph, JSONObject src, JSONObject tgt);

    boolean addInport (String name, String node, String port, JSONObject metadata, String graph);

    boolean removeInport (String name, String graph);

    boolean renameInport (String from, String to, String graph);

    boolean addOutport (String name, String node, String port, JSONObject metadata, String graph);

    boolean removeOutport (String name, String graph);

    boolean renameOutport (String from, String to, String graph);

    /**
     * Create a new group that is a kind of subgraph user can run independently.
     *
     * @param name the name of the new group
     * @param nodes the nodes in this group
     * @param metadata the metadata of the new group
     * @param graph the graph where the group is (can be another group)
     * @return True if successfully created
     */
    boolean addGroup (String name, JSONArray nodes, JSONObject metadata, String graph);

    /**
     * Remove an existing group
     *
     * @param name name of the group to delete
     * @param graph the graph where the group is
     * @return True if successfully deleted
     */
    boolean removeGroup (String name, String graph);

    /**
     * Rename a group.
     *
     * @param from the old name
     * @param to the new name
     * @param graph the graph where the group is
     * @return True if successfully changed
     */
    boolean renameGroup (String from, String to, String graph);

    /**
     * Change the metadata of a group.
     *
     * @param name name of the group
     * @param metadata new metadata of the group
     * @param graph the graph where the group is
     * @return True if successfully changed
     */
    boolean changeGroup (String name, JSONObject metadata, String graph);

    // Methods used to read information

    /**
     * Serialize the flow as json
     * @return the serialized flow
     */
    String serialize ();


}
