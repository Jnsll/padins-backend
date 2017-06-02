package fr.irisa.diverse.Core;

import fr.irisa.diverse.FBPNetworkProtocolUtils.Status;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Group;
import fr.irisa.diverse.Flow.Node;
import fr.irisa.diverse.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Map<Node, Boolean> alreadyRunned;

    // Constructor
    public FlowExecutionHandler (String graph, Workspace owningWorkspace, Flow flow) {
        this.owningWorkspace = owningWorkspace;
        this.flow = flow;
        this.alreadyRunned = new HashMap<>();

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
        // TODO
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
        // TODO
        // Must store when it started running
        // First : retrieve the nodes to execute in the right order
        // Then : run each block one by one. Giving to the method : the block to execute, its src and tgt
        // Must store that this graph is running

        // Retrieve the first nodes to execute
        ArrayList<Node> firstNodes = flow.findFirstNodesOfFlow(nodes);

        // Ajouter ce premier noeud a la pile
        // While qui regarde le set A_LANCER et le set EN_COURS, elle lance les noeuds des que c'est possible.
        // Des que c'est possible retire le noeud du set A_LANCER, le place dans EN_COURS et le lance dans un thread.
        // Ce thread (celui de node) termine par ajouter les noeuds suivants dans la liste A_LANCER. Cette methode add verifie qu'ils ne sont pas dedans.

    }

    private void stopNodes (ArrayList<Node> nodes) {
        // TODO
    }

    private void prepareNodesForExecution () {
        for (Node n : nodes) {
            n.prepareForExecution ();
        }
    }

    private void runNode (Node node) {
        // If the node is running, we wait for it to stop
        if(node.isRunning()){
            owningWorkspace.stopNode(node);
        }

        // Then we launch the execution

        // First : we verify that it really is a need to run the node. Maybe it didn't change
        if (node.shouldBeReRun()){
            // In order to do that, we wait for every previous node to have finished their own execution
            while(!havePreviousNodesFinish(node)) {
                Utils.wait(100);
            }

            // Now that we are sure that every previous node has finish running, we can actually run the given node
            owningWorkspace.executeNode(node);

            // Wait for it to finish
            while(node.isRunning()) {
                Utils.wait(100);
            }
        }

        // After the given node as finished running we start the following ones



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
