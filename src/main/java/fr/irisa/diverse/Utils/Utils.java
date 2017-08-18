package fr.irisa.diverse.Utils;

import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.Utils.Status;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Group;

/**
 * A Utils class to store methods that can be useful anywhere across the code, without the need to instantiate it.
 *
 * Created by antoine on 02/06/17.
 */
public class Utils {

    /**
     * Returns the status of the given graph.
     *
     * @param graph {Flow or Group} the graph to get the status for.
     * @return {Status} the status of the graph
     */
    public static Status getGraphStatus (Object graph) {
        // Retrieve the graph
        Status status = null;

        // Retrieve the status object
        if(graph instanceof Flow) {
            Flow f = (Flow) graph;
            status = ((Flow) graph).getStatus();
        } else if (graph instanceof Group) {
            Group g = (Group) graph;
            status = g.getStatus();
        }

        // Return it
        return status;
    }

    /**
     * Wait N milliseconds, N being the input.
     *
     * @param millis {int} the time to wait, in ms
     */
    public static void wait (int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transform an array of String into a single String.
     * @param input {String[]} the input the transform
     * @return {String} the single line String
     */
    public static String StringArrayToString (String[] input) {
        String res = "";

        for (int i=0; i< input.length; i++) {
            res += input[i] + "\n";
        }

        return res;
    }
}
