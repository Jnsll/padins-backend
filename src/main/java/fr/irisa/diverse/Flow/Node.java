package fr.irisa.diverse.Flow;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
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
    private boolean executable;

    private long lastRun;
    private long lastModification;

    private Date date;

    // Constructor
    public Node (String id, String component, JSONObject metadata, String graph, boolean executable, Flow owningFlow) {
        this.node = new JSONObject();
        this.owningFlow = owningFlow;
        this.component = component;
        this.metadata = metadata;
        this.graph = graph;
        this.inports = ComponentsUtils.getInPortsForComponent(owningFlow.getComponentsLibrary(), component, id);
        this.outports = ComponentsUtils.getOutPortsForComponent(owningFlow.getComponentsLibrary(), component, id);
        this.id = id;
        this.executable = executable;
        this.date = new Date();
        this.lastModification = date.getTime();
        this.lastRun = 0;

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

    public void setMetadata(JSONObject metadata) {
        lastModification = date.getTime();
        this.metadata = metadata;
    }

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

    public boolean isExecutable () {
        return executable;
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
        lastModification = date.getTime();

        Port p = findPort(port);
        if (p != null) {
            p.setConnectedEdge(edge);
        }
    }

    public ArrayList<Node> previousInFlow () {
        return nextOrPreviousNodeInFlow(getInports());
    }

    public ArrayList<Node> nextInFlow () {
        return nextOrPreviousNodeInFlow(getOutports());
    }

    public String getResult () {
        return (String) this.metadata.get("result");
    }

    public void setResult (String result) {
        this.metadata.put("result", result);
        lastRun = date.getTime();
    }

    public boolean isRunning () {
        return owningFlow.owningWorkspace.isNodeRunning(getId());
    }

    public boolean hasFinished () {
        return !isRunning();
    }

    public boolean shouldBeReRun () {
        boolean res = lastRun > lastModification;

        ArrayList<Node> previousNodes = previousInFlow();

        if(previousNodes != null) {
            for (Node previous : previousNodes) {
                res = res || previous.shouldBeReRun();
            }
        }

        return  res;
    }

    public void prepareForExecution () {
        // TODO
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
        for(int i=0; i<ports.size(); i++) {
            Port p = ports.get(i);
            if (p.getName().equals(name)) {
                return p;
            }
        }

        return null;
    }

    private ArrayList<Node> nextOrPreviousNodeInFlow (Ports ports) {
        // Create the object that will be returned
        ArrayList<Node> res = new ArrayList<>();
        boolean previous = ports == getInports();

        // Search the previous or next node for each ports in ports an add it into res
        for (int i=0; i<ports.size(); i++) {
            Node n = oppositeNodeForPort(ports.get(i), previous);
            if (n != null) res.add(n);
        }

        // End
        return res;
    }

    private Node oppositeNodeForPort (Port p, boolean previousNode) {
        // First : retrieve the edge
        String edgeId = p.getConnectedEdgeId();
        Edge e = owningFlow.getEdge(edgeId);

        // Second : determine whether we have to return the src or tgt node of this edge
        String resNodeId = (String) (previousNode ? e.getSrc().get("node") : e.getTgt().get("node"));

        return owningFlow.getNode(resNodeId, owningFlow.getId());
    }
}
