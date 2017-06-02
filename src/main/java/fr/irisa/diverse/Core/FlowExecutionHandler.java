package fr.irisa.diverse.Core;

import fr.irisa.diverse.FBPNetworkProtocolUtils.Status;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Group;
import fr.irisa.diverse.Flow.Node;
import fr.irisa.diverse.Utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Handle the execution of one group or flow.
 *
 * Created by antoine on 02/06/17.
 */
public class FlowExecutionHandler {

    // Attributes
    private ArrayList<Node> nodes;
    private Workspace owningWorkspace;
    private Flow flow;
    private Status status;

    private Set<Node> toLaunch;
    private Set<NodeExecutionThread> running;
    private boolean stop;

    // Constructor
    public FlowExecutionHandler (String graph, Workspace owningWorkspace, Flow flow) {
        this.owningWorkspace = owningWorkspace;
        this.flow = flow;
        this.toLaunch = new ConcurrentSkipListSet<>();
        this.running = new ConcurrentSkipListSet<>();
        this.stop = false;

        Object o = flow.getGraph(graph);

        if (o instanceof Flow) {
            nodes = ((Flow) o).getNodes();
            status = ((Flow) o).getStatus();
        } else if (o instanceof  Group){
            nodes = flow.getNodes((Group) o);
            status = ((Group) o).getStatus();
        }
    }

    /*==================================================================================================================
                                              PUBLIC CLASS METHODS
     =================================================================================================================*/

    public void run () {
        prepareNodesForExecution();

        runNodes();
    }

    public void stop () {
        stopNodes(nodes);
    }

    public void addToLaunch (Node n) {
        this.toLaunch.add(n);
    }

    public void runningThreadFinished (Thread t) {
        running.remove(t);
    }

    /*==================================================================================================================
                                                GETTERS AND SETTERS
     =================================================================================================================*/

    public boolean isRunning () {
        return status.isRunning();
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    private void runNodes () {
        // Retrieve the first nodes to execute
        ArrayList<Node> firstNodes = flow.findFirstNodesOfFlow(nodes);

        // Add each first node to the toLaunch list
        for( Node n : firstNodes) {
            toLaunch.add(n);
        }

        // Tell the status that we started
        status.start();

        // Start a while that look at the toLaunch list and start running a Node as soon as possible.
        while ((!toLaunch.isEmpty() || !running.isEmpty()) && !stop) {
            for (Node n : toLaunch) {
                // Verify that all the previous nodes in the flow have finished their execution
                if (havePreviousNodesFinish(n)) {
                    // If so, start running it
                    runNode(n);
                    toLaunch.remove(n);
                }
            }
        }

        // Here it is finished, we change the status
        status.stop();
    }

    private void stopNodes (ArrayList<Node> nodes) {
        // First : set stop to true to stop the while in runNodes
        this.stop = true;
        // Second : interrupt the Thread and remove them from the set.
        for (Thread t : running) {
            t.interrupt();
            running.remove(t);
        }

        // Third : make sure the nodes have been stopped
        for (Node n : nodes) {
            owningWorkspace.stopNode(n);
        }

        // Finally empty the toLaunch set
        toLaunch = new ConcurrentSkipListSet<>();
        running = new ConcurrentSkipListSet<>();
    }

    private void prepareNodesForExecution () {
        for (Node n : nodes) {
            n.prepareForExecution();
        }
    }

    private void runNode (Node node) {

        // If the node is running, we kill it
        if (node.isRunning()){
            owningWorkspace.stopNode(node);
        } else {
            // Create and run the thread
            NodeExecutionThread t = new NodeExecutionThread(node, this, owningWorkspace);
            running.add(t);
            t.start();
        }

    }

    private boolean havePreviousNodesFinish(Node n) {
        ArrayList<Node> previousNodes = n.previousInFlow();
        boolean res = true;

        if (previousNodes == null) return true;
        else {
            for (Node previous : previousNodes) {
                res = res && previous.hasFinished();
            }

            return res;
        }
    }
}
