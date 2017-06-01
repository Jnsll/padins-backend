package fr.irisa.diverse.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Workspace;
import org.json.simple.JSONObject;

import java.util.Date;

/**
 * Created by antoine on 26/05/2017.
 */
public class NetworkMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    private FBPNetworkProtocolManager owningManager;

    // Constructor
    public NetworkMessageHandler (FBPNetworkProtocolManager manager) {
        this.PROTOCOL = "network";
        this.owningManager = manager;
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    public void handleMessage (FBPMessage message) {
        String command = message.getCommand();

        switch (command) {
            case "persist" :
                persist(message);
                break;
            case "getstatus" :
                getstatus(message);
                break;
            case "edges" :
                edges(message);
                break;
            case "start" :
                start(message);
                break;
            case "stop" :
                stop(message);
                break;
            default:
                System.err.println("[ERROR] Unknown message on Network : " + message.toJSONString());
                break;
        }

    }

    /* =================================================================================================================
                                         HANDLERS FOR RECEIVED MESSAGE METHODS
       ===============================================================================================================*/

    private void persist(FBPMessage message) {
        // Might probably do something ... to do
        sendPersistMessage(message);
    }

    private void getstatus(FBPMessage message) {
    }

    private void edges(FBPMessage message) {
    }

    private void start(FBPMessage message) {
        JSONObject payload = message.getPayloadAsJSON();
        String graph = (String) payload.get("graph");

        boolean started = false;
        long startTime = 0;

        try {
            // Send a network started message
            startTime = sendStartedMessage(graph);
            started = true;
            // Actually start the graph
            owningManager.owningWorkspace.startGraph(graph);
            // Wait for the run to finish
            while (owningManager.owningWorkspace.graphRunning(graph)) {
                Thread.sleep(200);
            }
            // After it finishes : send a Stopped message
            sendStoppedMessage(graph, startTime);

        } catch (Workspace.NotExistingGraphException e) {
            if (started) {
                sendStoppedMessage(graph, startTime);
            }
            // Send the error to the clients
            owningManager.sendError(PROTOCOL, e.getMessage());
            // Print stack trace in stderr
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Thread.sleep exception
            e.printStackTrace();
        }
    }

    private void stop(FBPMessage message) {
    }

    /* =================================================================================================================
                                            METHODS TO CREATE RESPONSES
       ===============================================================================================================*/

    private void sendPersistMessage (FBPMessage message) {
        owningManager.sendToAll(message);
    }

    private long sendStartedMessage (String graph) {
        // Build the payload
        JSONObject payload = new JSONObject();
        Date date = new Date();
        long time = date.getTime();
        payload.put("time", time);
        payload.put("graph", graph);
        payload.put("started", true);
        payload.put("running", true); // TODO
        payload.put("debug", false); // TODO

        // Send it
        sendMessage("started", payload);

        // Return the started time. For use when calling sendStoppedMessage methods
        return time;
    }

    private void sendStoppedMessage (String graph, long time) {
        // Build the payload
        JSONObject payload = new JSONObject();
        Date date = new Date();
        long uptime = date.getTime() - time; // Not perfect calculation. Could be made better
        payload.put("time", time);
        payload.put("uptime", uptime);
        payload.put("graph", graph);
        payload.put("started", false);
        payload.put("running", false); // TODO
        payload.put("debug", false); // TODO

        // Send it
        sendMessage("started", payload);
    }

}