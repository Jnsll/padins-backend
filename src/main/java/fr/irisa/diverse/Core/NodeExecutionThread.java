package fr.irisa.diverse.Core;

import fr.irisa.diverse.Flow.Node;
import fr.irisa.diverse.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by antoine on 02/06/2017.
 */
public class NodeExecutionThread extends Thread implements Comparable<NodeExecutionThread> {

    // Attributes
    private Node node;
    private FlowExecutionHandler executionHandler;
    private Workspace workspace;

    public NodeExecutionThread (Node n, FlowExecutionHandler executionHandler, Workspace workspace) {
        this.node = n;
        this.executionHandler = executionHandler;
        this.workspace = workspace;
    }

    @Override
    public void run() {

        // First : we verify that there really is a need to run the node. Maybe it didn't change and neither its previous nodes
        if (node.shouldBeReRun()){
            long beginsRunning = new Date().getTime();

            // Now that we are sure that every previous node has finish running, we can actually run the given node
            workspace.executeNode(node);

            // Now we wait for the Kernel to finish executing the code of this node.
            while (workspace.isNodeRunning(node.getId()) && !node.receivedResultAfterTime(beginsRunning)) {
                Utils.wait(100);
            }
        }

        // After it finishes and we're sure the node receives the result,
        // we add the next nodes to the Set of node to launch
        // and remove this Thread from the running ones.
        ArrayList<Node> nextInFlow = node.nextInFlow();
        if (nextInFlow != null ) {
            for (Node n : node.nextInFlow()) {
                executionHandler.addToLaunch(n);
            }
        }

        executionHandler.runningThreadFinished(Thread.currentThread());
    }

    @Override
    public int compareTo(NodeExecutionThread o) {
        return node.compareTo(o.node);
    }
}
