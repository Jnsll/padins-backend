package Flow;

import org.json.simple.JSONObject;

/**
 * Created by antoine on 30/05/17.
 */
interface FlowInterface {

    // Methods used to modify the graph

    boolean addNode (String id, String component, JSONObject metadata, String graph);

    boolean removeNode (String id, String graph);

    boolean renameNode (String from, String to, String graph);

    boolean changeNode (String id, JSONObject metadata, String graph);

    boolean addEdge (JSONObject src, JSONObject tgt, JSONObject metadata, String graph);

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

    boolean addGroup (String name, JSONObject nodes, JSONObject metadata, String graph);

    boolean removeGroup (String name, String graph);

    boolean renameGroup (String from, String to, String graph);

    boolean changeGroup (String name, JSONObject metadata, String graph);

    // Methods used to read information

    String serialize ();


}
