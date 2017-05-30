package Flow;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by antoine on 30/05/17.
 */
public interface FlowInterface {

    // Methods used to modify the graph

    void addNode (String id, String component, JSONObject metadata, String graph);

    void removeNode (String id, String graph);

    void renameNode (String from, String to, String graph);

    void changeNode (String id, JSONObject metadata, String graph);

    void addEdge (JSONObject src, JSONObject tgt, JSONObject metadata, String graph);

    void removeEdge (String graph, JSONObject src, JSONObject tgt);

    void changeEdge (String graph, JSONObject metadata, JSONObject src, JSONObject tgt);

    void addInitial (String graph, JSONObject metadata, JSONObject src, JSONObject tgt);

    void removeInitial (String graph, JSONObject src, JSONObject tgt);

    void addInport (String name, String node, String port, JSONObject metadata, String graph);

    void removeInport (String name, String graph);

    void renameInport (String from, String to, String graph);

    void addOutport (String name, String node, String port, JSONObject metadata, String graph);

    void removeOutport (String name, String graph);

    void renameOutport (String from, String to, String graph);

    void addGroup (String name, JSONObject nodes, JSONObject metadata, String graph);

    void removeGroup (String name, String graph);

    void renameGroup (String from, String to, String graph);

    void changeGroup (String name, JSONObject metadata, String graph);

    // Methods used to read information

    String serialize ();


}
