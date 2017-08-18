package fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.Utils.Status;
import fr.irisa.diverse.Utils.Utils;
import org.json.simple.JSONObject;

/**
 * Class managing the Network messages for the Flow-Based Programming Network Protocol
 * To know more about this protocol, take a look at the doc on J.Paul Morisson's website :
 * https://flowbased.github.io/fbp-protocol/#sub-protocols
 *
 * Created by antoine on 26/05/2017.
 */
@SuppressWarnings("unchecked")
public class NetworkMessageHandler extends SendMessageOverFBP implements FBPProtocolHandler {

    // Attributes
    // private FBPNetworkProtocolManager owningManager; From SendMessageOverFBP

    /* =================================================================================================================
                                                    CONSTRUCTOR
       ===============================================================================================================*/

    NetworkMessageHandler (FBPNetworkProtocolManager manager) {
        this.PROTOCOL = "network";
        this.owningManager = manager;
    }

    /* =================================================================================================================
                                    FBPProtocolHandler INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    /**
     * Handle a message.
     * It call the corresponding method for each supported type of message.
     *
     * @param message : the message to handle
     */
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

    public void sendOutput (String message, String type, String url) {
        sendOutputMessage (message, type, url);
    }

    /* =================================================================================================================
                                         HANDLERS FOR RECEIVED MESSAGE METHODS
       ===============================================================================================================*/

    /**
     * Tells the runtime to persist the current state of graphs and components so that they are available between restarts.
     * It saves the flow.json file.
     *
     * https://flowbased.github.io/fbp-protocol/#network-persist
     *
     * @param message {FBPMessage} the received message.
     */
    private void persist(FBPMessage message) {
        this.owningManager.owningWorkspace.save();

        sendPersistMessage(message);
    }

    /**
     * Handle a "getstatus" message by sending the current status of the runtime.
     *
     * https://flowbased.github.io/fbp-protocol/#network-getstatus
     *
     * @param message {FBPMessage} the received message.
     */
    private void getstatus(FBPMessage message) {
        JSONObject payload = message.getPayload();
        String graph = (String) payload.get("graph");

        sendStatusMessage(graph);
    }

    /**
     * Handle a "edges" message. It does nothing for now.
     *
     * https://flowbased.github.io/fbp-protocol/#network-edges
     *
     * @param message {FBPMessage} the received message.
     */
    private void edges(FBPMessage message) {
        // Not implemented for now. Maybe not useful in our case
        // TODO
    }

    /**
     * Handle a "start" message by starting the execution of the graph.
     *
     * https://flowbased.github.io/fbp-protocol/#network-start
     *
     * @param message {FBPMessage} the received message.
     */
    private void start(FBPMessage message) {
        Runnable task = () -> {
            JSONObject payload = message.getPayload();
            String graph = (String) payload.get("graph");

            boolean started = false;

            try {
                // Send a network started message
                sendStartedMessage(graph);
                // Start the graph
                owningManager.owningWorkspace.startGraph(graph);
                started = true;
                // Wait for the run to finish
                while (owningManager.owningWorkspace.graphRunning(graph)) {
                    Thread.sleep(200);
                }
                // After it finishes : send a Stopped message
                sendStoppedMessage(graph);
                System.out.println("Simulation finished");

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
        };

        Thread thread = new Thread(task);
        thread.start();

    }

    /**
     * Handle a "stop" message by stopping the execution of the graph or group.
     *
     * https://flowbased.github.io/fbp-protocol/#network-stop
     *
     * @param message {FBPMessage} the received message.
     */
    private void stop(FBPMessage message) {
        JSONObject payload = message.getPayload();
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

    /**
     * Handle a "debug" message. Do nothing for now.
     *
     * https://flowbased.github.io/fbp-protocol/#network-debug
     *
     * @param message {FBPMessage} the received message.
     */
    private void debug (FBPMessage message) {
        // Not functional for now
        sendError("Debug mode not functional");
    }

    /* =================================================================================================================
                                            METHODS TO CREATE RESPONSES
       ===============================================================================================================*/

    /**
     * Send a persist message. It is the exact same message than the one received from the UI.
     *
     * https://flowbased.github.io/fbp-protocol/#network-persist
     *
     * @param message {FBPMessage} the received message.
     */
    private void sendPersistMessage (FBPMessage message) {
        owningManager.sendToAll(message);
    }

    /**
     * Send a "started" message. Inform that a given network has started.
     * Must be call when the graph start.
     *
     * https://flowbased.github.io/fbp-protocol/#network-started
     *
     * @param graph {String} id of the started graph
     */
    private void sendStartedMessage (String graph) {
        Status status = getGraphStatus(graph);

        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("time", String.valueOf(status.getStartedTime()));
        payload.put("graph", graph);
        payload.put("started", status.hasStarted());
        payload.put("running", true);
        payload.put("debug", status.isInDebugMode());

        // Send it
        sendMessage("started", payload);
    }

    /**
     * Send a "stopped" message. Inform that a given network has stopped.
     * Must be call when the graph stop.
     *
     * https://flowbased.github.io/fbp-protocol/#network-stopped
     *
     * @param graph {String} id of the started graph
     */
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

    /**
     * Send a "status" message. It's a response to a "getstatus" message.
     *
     * https://flowbased.github.io/fbp-protocol/#network-status
     *
     * @param graph {String} id of the action targets
     */
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

    /**
     * Send an "output" message. It is an output message from a running network, roughly similar to STDOUT output
     * of a Unix process, or a line of console.log in JavaScript. Output can also be used for passing images from
     * the runtime to the UI.'
     *
     * https://flowbased.github.io/fbp-protocol/#network-output
     *
     * @param message {String} contents of the output line
     * @param type {String} type of output, either message or preview url
     * @param url {String} URL for an image generated by the runtime
     */
    private void sendOutputMessage (String message, String type, String url) {
        // Build the payload
        JSONObject payload = new JSONObject();
        payload.put("message", message);
        payload.put("type",type);
        payload.put("url", url);

        // Send it
        sendMessageToAll("output", payload);
    }

    /* TODO : features to implement later :
        - processerror : https://flowbased.github.io/fbp-protocol/#network-processerror
        - icon : https://flowbased.github.io/fbp-protocol/#network-icon
        - connect : https://flowbased.github.io/fbp-protocol/#network-connect
        - begingroup : https://flowbased.github.io/fbp-protocol/#network-begingroup
        - data : https://flowbased.github.io/fbp-protocol/#network-data
        - endgroup : https://flowbased.github.io/fbp-protocol/#network-endgroup
        - disconnect : https://flowbased.github.io/fbp-protocol/#network-disconnect
    */

    /* =================================================================================================================
                                            CLASS CUSTOM METHODS
       ===============================================================================================================*/

    /**
     * Get the status of the given graph.
     *
     * @param graph {String} the id of the targeted graph.
     * @return {Status} the status instance of the given graph.
     */
    private Status getGraphStatus (String graph) {
        // Retrieve the graph
        Object o = owningManager.owningWorkspace.getFlow().getGraph(graph);

        return Utils.getGraphStatus(o);
    }
}