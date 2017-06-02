package fr.irisa.diverse.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.FBPNetworkProtocolUtils.Status;
import fr.irisa.diverse.Utils.Utils;
import org.json.simple.JSONObject;

/**
 * Implementation of the handler for messages on the network sub-protocol of the FBP Network Protocol
 * https://flowbased.github.io/fbp-protocol/#network-start
 *
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
            case "debug" :
                debug(message);
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
        JSONObject payload = message.getPayloadAsJSON();
        String graph = (String) payload.get("graph");

        sendStatusMessage(graph);
    }

    private void edges(FBPMessage message) {
    }

    private void start(FBPMessage message) {
        JSONObject payload = message.getPayloadAsJSON();
        String graph = (String) payload.get("graph");

        boolean started = false;

        try {
            // Start the graph
            owningManager.owningWorkspace.startGraph(graph);
            started = true;
            // Send a network started message
            sendStartedMessage(graph);
            // Wait for the run to finish
            while (owningManager.owningWorkspace.graphRunning(graph)) {
                Thread.sleep(200);
            }
            // After it finishes : send a Stopped message
            sendStoppedMessage(graph);

        } catch (Workspace.NotExistingGraphException e) {
            if (started) {
                sendStoppedMessage(graph);
            }
            // Send the error to the clients
            sendError(e.getMessage());
            // Print stack trace in stderr
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Thread.sleep exception
            e.printStackTrace();
        }
    }

    private void stop(FBPMessage message) {
        JSONObject payload = message.getPayloadAsJSON();
        // Retrieve the id of the graph
        String graph = (String) payload.get("graph");

        // Then we make sure the graph is really running
        if (owningManager.owningWorkspace.graphRunning(graph)) {
            // If so, we stop it
            try {
                owningManager.owningWorkspace.stopGraph(graph);
                // Wait for the graph to finish
                while (owningManager.owningWorkspace.graphRunning(graph)) {
                    Thread.sleep(200);
                }
                // Send a stopped message after it finishes
                sendStoppedMessage(graph);

            } catch (InterruptedException e) {
                // Thread.sleep exception
                e.printStackTrace();
            }

        }
    }

    private void debug (FBPMessage message) {
        // Not functional for now
        sendError("Debug mode not functional");
    }

    /* =================================================================================================================
                                            METHODS TO CREATE RESPONSES
       ===============================================================================================================*/

    private void sendPersistMessage (FBPMessage message) {
        owningManager.sendToAll(message);
    }

    private void sendStartedMessage (String graph) {
        Status status = getGraphStatus(graph);

        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("time", String.valueOf(status.getStartedTime()));
        payload.put("graph", graph);
        payload.put("started", status.hasStarted());
        payload.put("running", status.isRunning());
        payload.put("debug", status.isInDebugMode());

        // Send it
        sendMessage("started", payload);
    }

    private void sendStoppedMessage (String graph) {
        Status status = getGraphStatus(graph);

        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("time", String.valueOf(status.getStoppedTime()));
        payload.put("uptime", status.getUptime());
        payload.put("graph", graph);
        payload.put("started", status.hasStarted());
        payload.put("running", status.isRunning());
        payload.put("debug", status.isInDebugMode());

        // Send it
        sendMessage("stopped", payload);
    }

    private void sendStatusMessage (String graph) {
        Status status = getGraphStatus(graph);

        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("graph", graph);
        payload.put("uptime", status.getUptime());
        payload.put("started", status.hasStarted());
        payload.put("running", status.isRunning());
        payload.put("debug", status.isInDebugMode());

        // Send it
        sendMessage("status", payload);
    }

    /* =================================================================================================================
                                            CLASS CUSTOM METHODS
       ===============================================================================================================*/

    private Status getGraphStatus (String graph) {
        // Retrieve the graph
        Object o = owningManager.owningWorkspace.getFlow().getGraph(graph);

        return Utils.getGraphStatus(o);
    }
}