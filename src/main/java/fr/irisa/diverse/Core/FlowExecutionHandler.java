package fr.irisa.diverse.Core;

import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.Utils.Status;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Group;
import fr.irisa.diverse.Flow.Node;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Handle the execution of one group or flow. A group or flow is composed of several nodes. One node is one block on
 * the UI.
 *
 * You have to create one instance of this class per flow you want to run.
 * Then, when you want to run a flow, it works has following :
 * 1 - Search for the first Nodes to execute (the ones with no inputs connected)
 * 2 - Run these first nodes.
 *      To do that, we put all the nodes to execute in a Set.
 *      Beside that, we have a master that look at each node in the Set and run it if possible.
 *      The execution of a node is done in a new thread.
 *      After starting the execution of a node, it put the thread into a Running Set.
 *      When the execution of a Node in a thread finishes, it adds the nodes following the one that just runned into the
 *      toLaunch Set.
 *      In order to run a node, the master verify that its dependencies have finished their own execution.
 *      If not, it continues going through the Set, looking for nodes that can be launched.
 *      When both Set (toLaunch and running) are empty, we stop the master and the execution of the flow is finished.
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
    private Map<Node, NodeExecutionThread> runningMap;
    private boolean stop;

    /*==================================================================================================================
                                                    CONSTRUCTOR
     =================================================================================================================*/
    public FlowExecutionHandler (String graph, Workspace owningWorkspace, Flow flow) {
        this.owningWorkspace = owningWorkspace;
        this.flow = flow;
        this.toLaunch = new ConcurrentSkipListSet<>();
        this.running = new ConcurrentSkipListSet<>();
        this.runningMap = new HashMap<>();
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

    /**
     * Start the execution of the flow given to the constructor
     */
    public void run () {
        stop = false;
        prepareNodesForExecution();

        runNodes();
    }

    /**
     * Stop the flow's execution
     */
    public void stop () {
        stopNodes(nodes);
    }

    /**
     * Add a node to the list of nodes that will be started as soon as possible
     *
     * @param n : the Node to add
     */
    synchronized public void addToLaunch (Node n) {
        this.toLaunch.add(n);
    }

    /**
     * Method for the Thread to prevent that it finished.
     * It will remove it from the list of running nodes (1 node <-> 1 thread).
     *
     * @param t : the Thread that finished.
     */
    public void runningThreadFinished (Thread t) {
        removeThread(t);
    }

    /*==================================================================================================================
                                                GETTERS AND SETTERS
     =================================================================================================================*/

    /**
     * @return a boolean telling whether the Execution of the Flow is running or not
     */
    public boolean isRunning () {
        return status.isRunning();
    }

    /*==================================================================================================================
                                              PRIVATE CLASS METHODS
     =================================================================================================================*/

    /**
     * Starts the flow to execute.
     *
     * It begins with retrieving the first nodes to execute.
     * Then put them into the toLaunch set and start doing the master job, as described above.
     */
    private void runNodes () {
        // Retrieve the first nodes to execute
        ArrayList<Node> firstNodes = flow.findFirstNodesOfFlow(nodes);

        // Add each first node to the toLaunch list
        for( Node n : firstNodes) {
            toLaunch.add(n);
        }

        // Tell the status that we started
        status.start();

        // Start a while loop that look at the toLaunch list and start running a Node as soon as possible.
        while ((!toLaunch.isEmpty() || !running.isEmpty()) && !stop) {
            for (Node n : toLaunch) {
                // Verify that all the previous nodes in the flow have finished their execution
                if (havePreviousNodesFinish(n)) {
                    // If so, start running it
                    System.out.println("\nNode " + n.getComponent() + " will be launched");
                    runNode(n);
                    toLaunch.remove(n);
                }
            }
        }

        System.out.println("Stopping execution !");

        // Here it is finished, we change the status
        status.stop();
    }

    /**
     * Stop the execution of the given nodes
     *
     * @param nodes : The List of nodes to stop
     */
    private void stopNodes (ArrayList<Node> nodes) {
        // First : set stop to true to stop the while in runNodes
        this.stop = true;
        // Second : interrupt the Thread and remove them from the instance.
        for (Thread t : running) {
            System.out.println("Interrupting thread");
            t.interrupt();
            removeThread(t);
        }

        // Third : make sure the nodes have been stopped
        for (Node n : nodes) {
            toLaunch.remove(n);
            owningWorkspace.stopNode(n);
        }
    }

    public void errorExecutingNode (Node n) {
        if (n != null && nodes.indexOf(n) != -1) {
            stop();
        }
    }

    /**
     * Remove a given thread from everywhere it is stored in the class.
     *
     * @param t : the Thread to remove
     */
    private void removeThread (Thread t) {
        // Remove the thread in the runningMap
        Set<Node> keys = runningMap.keySet();
        for (Node n : keys) {
            if (runningMap.get(n).equals(t)) { runningMap.remove(n, t); }
        }

        // Remove the thread in the running set
        running.remove(t);
    }

    private void prepareNodesForExecution () {
        for (Node n : nodes) {
            n.prepareForExecution();
        }
    }

    /**
     * Run a unique node.
     * It starts a new Thread for the node and add it to the Running set.
     * @param node : the Node to execute
     */
    private void runNode (Node node) {

        // If the node is running, we kill it
        if (node.isRunning()){
            owningWorkspace.stopNode(node);
        }

        // Send a message to the UI to let it know that the node is being executed
        owningWorkspace.clientCommunicationManager.sendStartNode(node.getId());

        // Create and run the thread
        NodeExecutionThread t = new NodeExecutionThread(node, this, owningWorkspace);
        running.add(t);
        t.start();

    }

    /**
     * Tells whether the dependency of a node finished running.
     * This method is usually called in order to know if it is possible de run a node.
     *
     * @param n : the Node for which you want to know if dependencies finished running.
     * @return True if all the dependencies finished
     */
    private boolean havePreviousNodesFinish(Node n) {
        // Retrieve the previousNodes of the given node n.
        ArrayList<Node> previousNodes = n.previousInFlow();
        boolean res = true;

        // Single case : if there is no previous node, we consider that previous ones have finished,
        // because we can start the execution of this node.
        if (previousNodes == null) return true;
        else {
            // Most common case
            for (Node previous : previousNodes) {
                // For each previous node, we look if the node has finished. To determine that, this previous node
                // also take a look at its dependencies, that look at theirs and so one.
                // Thanks to that we make sure that we return true only if all previous node finished their execution,
                // not just the previous one. It reduces the probability of error.
                // Then we do an arithmetic operation to store the result.
                res = res && previous.hasFinished();
            }

            // End
            return res;
        }
    }
} // End class
