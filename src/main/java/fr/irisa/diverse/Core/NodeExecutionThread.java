package fr.irisa.diverse.Core;

import fr.irisa.diverse.Flow.Node;
import fr.irisa.diverse.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Thread that manage the execution of a node via the Jupyter kernel.
 *
 * Created by antoine on 02/06/2017.
 */
public class NodeExecutionThread extends Thread implements Comparable<NodeExecutionThread> {

    // Attributes
    private Node node;
    private FlowExecutionHandler executionHandler;
    private Workspace workspace;

    // Constructor
    public NodeExecutionThread (Node n, FlowExecutionHandler executionHandler, Workspace workspace) {
        this.node = n;
        this.executionHandler = executionHandler;
        this.workspace = workspace;
    }

    /**
     * The run method is the main method of a Thread.
     * It verify that the node should be re-runned, if so launch it. If not, directly go to next step.
     * The next step is adding the following nodes in the toLaunch set
     * and removing this thread from the list of running thread in the FlowExecutionHandler and
     */
    @Override
    public void run() {
        if (node.noKnownError()) {
            // First : we verify that there really is a need to run the node. Maybe it didn't change and neither its previous nodes
            if (node.shouldBeReRun()){
                long beginsRunning = new Date().getTime();

                // Now that we are sure that every previous node has finish running, we can actually run the given node
                workspace.executeNode(node);

                // Now we wait for the Kernel to finish executing the code of this node.
                while (workspace.isNodeRunning(node.getId()) || !node.receivedResultAfterTime(beginsRunning)) {
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

            // Send a message to the UIs to let the connected users know that the nodes finished running
            workspace.clientCommunicationManager.sendFinishNode(node.getId());

            executionHandler.runningThreadFinished(Thread.currentThread());
        } else {
            // If the previous execution thrown an error that has not been corrected, we stop the execution
            workspace.errorExecutingNode(node.getId());
        }

    }

    /**
     * CompareTo method from Comparable interface. It is used by an ordered Set to determine where to add an instance
     * of this class.
     *
     * @param o : the object to compare to
     * @return -1 if this is inferior to o, 0 if equal, 1 if greater
     */
    @Override
    public int compareTo(NodeExecutionThread o) {
        return node.compareTo(o.node);
    }
}
