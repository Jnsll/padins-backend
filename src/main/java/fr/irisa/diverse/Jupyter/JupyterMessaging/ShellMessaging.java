package fr.irisa.diverse.Jupyter.JupyterMessaging;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPNetworkProtocolManager;
import fr.irisa.diverse.Jupyter.JupyterChannels.ShellChannel;
import org.json.simple.JSONObject;

/** This class provide a function to create every request message for the shell socket of Jupyter, as documented here :
 * http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-shell-router-dealer-channel
 *
 * The list of messages is :
 *  execute_request
 *  inspect_request
 *  complete_request
 *  history_request
 *  is_complete_request
 *  connect_request
 *  comm_info_request
 *  kernel_info_request
 *  shutdown_request
 *
 * Created by antoine on 10/05/2017.
 */
public class ShellMessaging {

    /* =================================================================================================================
                                               ATTRIBUTES
     =================================================================================================================*/

    private Kernel kernel = null;
    private ShellChannel channel = null;
    private FBPNetworkProtocolManager manager;

    /* =================================================================================================================
                                                CONSTRUCTOR
     =================================================================================================================*/

    public ShellMessaging(Kernel kernel, ShellChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
        this.manager = new FBPNetworkProtocolManager(kernel.owningWorkspace);
    }

    /* =================================================================================================================
                                                HANDLE MESSAGE METHOD
     =================================================================================================================*/

    /**
     * Handle the given message, coming from the Shell or Control Channel
     * @param type {String} the type of the message
     * @param message {JupyterMessage} the message itself
     */
    public void handleMessage (String type, JupyterMessage message) {
        switch (type) {
            case "execute_reply" :
                handleExecuteReplyMessage(message);
                break;
            case "inspect_reply" :
                handleIntrospectionReplyMessage(message);
                break;
            case "complete_reply" :
                handleCompletionReplyMessage(message);
                break;
            case "history_reply" :
                handleHistoryReplyMessage(message);
                break;
            case "is_complete_reply" :
                handleCodeCompletenessReplyMessage(message);
                break;
            case "connect_reply" :
                handleConnectionReplyMessage(message);
                break;
            case "comm_info_reply" :
                handleCommInfoReplyMessage(message);
                break;
            case "kernel_info_reply" :
                handleKernelInfoReplyMessage(message);
                break;
            case "shutdown_reply" :
                handleShutdownReplyMessage(message);
                break;
            default :
                String[] msg = message.getMessageToSend();
                String res = "";
                for (String aMsg : msg) {
                    res += aMsg;
                }
                System.err.println("Received unknown message on shell channel : " + res);
        }
    }

    /* =================================================================================================================
                                         METHODS TO SEND SHELL REQUEST MESSAGES
     =================================================================================================================*/

    /**
     * Implementation of execute_request message according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#execute
     * @param code : python code to execute
     * @return : the message sent through the channel
     */
    public String sendExecuteRequestMessage (String code) {
        JupyterMessage message = new JupyterMessage(kernel, "execute_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("silent", false);
        content.put("store_history", true);
        content.put("user_expressions", "");
        content.put("allow_stdin", true);
        content.put("stop_on_error", false);

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection inspect_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#introspection
     * @return : the message sent through the channel
     */
    public String sendIntrospectionRequestMessage (String code, int cursorPos) {
        JupyterMessage message = new JupyterMessage(kernel, "inspect_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("cursor_pos", Integer.toString(cursorPos));
        content.put("detail_level", "1");

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#completion
     * @return : the message sent through the channel
     */
    public String sendCompletionRequestMessage (String code, int cursorPos) {
        JupyterMessage message = new JupyterMessage(kernel, "complete_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("cursor_pos", Integer.toString(cursorPos));

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#history
     * @return : the message sent through the channel
     */
    public String sendHistoryRequestMessage (int nbOfCells) {
        JupyterMessage message = new JupyterMessage(kernel, "history_request");

        JSONObject content = new JSONObject();
        content.put("output", true);
        content.put("raw", false);
        content.put("hist_access_type", "tail");
        content.put("n", Integer.toString(nbOfCells));

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-completeness
     * @return : the message sent through the channel
     */
    public String sendCodeCompletenessRequestMessage (String code) {
        JupyterMessage message = new JupyterMessage(kernel, "is_complete_request");

        JSONObject content = new JSONObject();
        content.put("code", code);

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#connect
     * @return : the message sent through the channel
     */
    public String sendConnectRequestMessage () {
        JupyterMessage message = new JupyterMessage(kernel, "connect_request");

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#comm-info
     * @return : the message sent through the channel
     */
    public String sendCommInfoRequestMessage () {
        JupyterMessage message = new JupyterMessage(kernel, "comm_info_request");

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info
     * @return : the message sent through the channel
     */
    public String sendKernelInfoRequestMessage () {
        JupyterMessage message = new JupyterMessage(kernel, "kernel_info_request");

        //channel.send(message.getMessageToSend());

        return message.toString();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-shutdown
     * @return : the message sent through the channel
     */
    public String sendKernelShutdownRequestMessage (boolean restart) {
        JupyterMessage message = new JupyterMessage(kernel, "shutdown_request");

        JSONObject content = new JSONObject();
        content.put("restart", Boolean.toString(restart));
        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.toString();
    }

    /* =================================================================================================================
                                        METHODS TO HANDLE SHELL REPLY MESSAGES
     =================================================================================================================*/

    /**
     * Handle an excute_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#execution-results
     *
     * Our implementation behavior: depends on the status of the message.
     * - OK : do nothing
     * - ERROR : Broadcast the error to the UIs and log an error
     * - ABORT : Broadcast the abort to the UIs and log an error
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleExecuteReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();

        String status = (String) content.get("status");
        Long executionCount = (Long) content.get("execution_count");
        kernel.setNbExecutions(executionCount);

        switch (status) {
            case "ok":
                // Good news everything went well
                break;
            case "error":
                System.err.println("Error executing code of cell nº" + executionCount);
                manager.sendErrorToAll("network", "[JUPYTER ERROR] For node : " + kernel.linkedNodeId + ", impossible to run code");
                kernel.owningWorkspace.errorExecutingNode(kernel.linkedNodeId);
                break;
            case "abort":
                System.err.println("Execution of the code of cell nº" + executionCount + " has been aborted");
                manager.sendErrorToAll("network", "[JUPYTER ERROR] For node : " + kernel.linkedNodeId + ", code running aborted");
                kernel.owningWorkspace.errorExecutingNode(kernel.linkedNodeId);
                break;
        }
    }

    /**
     * Handle an introspection_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#introspection
     *
     * Our implementation behavior: depends on the status of the message.
     * - OK : TODO send result to UIs
     * - ERROR : Broadcast an error to the UIs
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleIntrospectionReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();

        String status = (String) content.get("status");

        if(status.equals("error")) {
            manager.sendError("NETWORK", "[JUPYTER ERROR] Introspection error for node : " + kernel.linkedNodeId + "");
            return;
        }
        else if (status.equals("ok")) {
            boolean found = (boolean) content.get("found");
            if(found) {
                String data = (String) content.get("data");
                // TODO send data field to UI
                return;
            }
        }
    }

    /**
     * Handle an completion_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#completion
     *
     * Our implementation behavior: depends on the status of the message.
     * - OK : TODO send info to corresponding UI, probably sending UI username via metadata and retrieving it here
     * - ERROR : Broadcast an error to the UIs
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleCompletionReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();

        String status = (String) content.get("status");

        if(status.equals("error")) {
            manager.sendError("NETWORK", "[JUPYTER ERROR] Completion error for node : " + kernel.linkedNodeId + "");
            return;
        }
        else if (status.equals("ok")) {
            // TODO send info to corresponding UI, probably sending UI username via metadata and retrieving it here
        }


    }

    /**
     * Handle an history_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#history
     *
     * Our implementation behavior: depends on the status of the message.
     * TODO
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleHistoryReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();
        // TODO
    }

    /**
     * Handle a is_complete_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-completeness
     *
     * Our implementation behavior: depends on the status of the message.
     * TODO
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleCodeCompletenessReplyMessage (JupyterMessage message) {
        // TODO
    }

    /**
     * Handle a connect_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#connect
     *
     * Our implementation behavior: depends on the status of the message.
     * TODO
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleConnectionReplyMessage (JupyterMessage message) {
        // TODO
    }

    /**
     * Handle a comm_info_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#comm-info
     *
     * Our implementation behavior: depends on the status of the message.
     * TODO
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleCommInfoReplyMessage (JupyterMessage message) {
        // TODO
    }

    /**
     * Handle a kernel_info_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info
     *
     * Our implementation behavior: depends on the status of the message.
     * TODO
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleKernelInfoReplyMessage (JupyterMessage message) {
        // TODO
    }

    /**
     * Handle a shutdown_reply message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-shutdown
     *
     * Our implementation behavior: depends on the status of the message.
     * TODO
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleShutdownReplyMessage (JupyterMessage message) {
        // TODO
    }
}
