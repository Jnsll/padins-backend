package fr.irisa.diverse.Utils;

import fr.irisa.diverse.FBPNetworkProtocolUtils.Status;
import fr.irisa.diverse.Flow.Flow;
import fr.irisa.diverse.Flow.Group;

/**
 * Created by antoine on 02/06/17.
 */
public class Utils {

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

    public static void wait (int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
