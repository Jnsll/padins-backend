package JupyterMessaging;

import Core.Kernel;
import JupyterChannels.ShellChannel;
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

    // Attributes
    private Kernel kernel = null;
    private ShellChannel channel = null;

    // Constructor
    public ShellMessaging(Kernel kernel, ShellChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
    }

    /* =================================================================================================================
                                                HANDLE MESSAGE METHOD
     =================================================================================================================*/

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
                System.err.println("Received unknown message on shell channel : " + message.getMessageToSend());
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
    public String[] sendExecuteRequestMessage (String code) {
        JupyterMessage message = new JupyterMessage(kernel, "execute_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("silent", "False");
        content.put("store_history", "True");
        content.put("user_expressions", "");
        content.put("allow_stding", "True");
        content.put("stop_on_error", "False");

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection inspect_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#introspection
     * @return : the message sent through the channel
     */
    public String[] sendIntrospectionRequestMessage (String code, int cursorPos) {
        JupyterMessage message = new JupyterMessage(kernel, "inspect_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("cursor_pos", Integer.toString(cursorPos));
        content.put("detail_level", "1");

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#completion
     * @return : the message sent through the channel
     */
    public String[] sendCompletionRequestMessage (String code, int cursorPos) {
        JupyterMessage message = new JupyterMessage(kernel, "complete_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("cursor_pos", Integer.toString(cursorPos));

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#history
     * @return : the message sent through the channel
     */
    public String[] sendHistoryRequestMessage (int nbOfCells) {
        JupyterMessage message = new JupyterMessage(kernel, "history_request");

        JSONObject content = new JSONObject();
        content.put("output", "True");
        content.put("raw", "False");
        content.put("hist_access_type", "tail");
        content.put("n", Integer.toString(nbOfCells));

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-completeness
     * @return : the message sent through the channel
     */
    public String[] sendCodeCompletenessRequestMessage (String code) {
        JupyterMessage message = new JupyterMessage(kernel, "is_complete_request");

        JSONObject content = new JSONObject();
        content.put("code", code);

        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#connect
     * @return : the message sent through the channel
     */
    public String[] sendConnectRequestMessage () {
        JupyterMessage message = new JupyterMessage(kernel, "connect_request");

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#comm-info
     * @return : the message sent through the channel
     */
    public String[] sendCommInfoRequestMessage () {
        JupyterMessage message = new JupyterMessage(kernel, "comm_info_request");

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-info
     * @return : the message sent through the channel
     */
    public String[] sendKernelInfoRequestMessage () {
        JupyterMessage message = new JupyterMessage(kernel, "kernel_info_request");

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-shutdown
     * @return : the message sent through the channel
     */
    public String[] sendKernelShutdownRequestMessage (boolean restart) {
        JupyterMessage message = new JupyterMessage(kernel, "shutdown_request");

        JSONObject content = new JSONObject();
        content.put("restart", Boolean.toString(restart));
        message.setContent(content);

        channel.send(message.getMessageToSend());

        return message.getMessageToSend();
    }

    /* =================================================================================================================
                                        METHODS TO HANDLE SHELL REPLY MESSAGES
     =================================================================================================================*/

    private void handleExecuteReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();

        String status = (String) content.get("status");
        int executionCount = new Integer(content.get("execution_count").toString());
        kernel.setNbExecutions(executionCount);

        if (status == "ok") {
            // Good news everything went well
        } else if (status == "error") {
            System.err.println("Error executing code of cell nº" + executionCount);
            // TODO : send message to web UI
        } else if (status == "abort") {
            System.err.println("Execution of the code of cell nº" + executionCount + " has been aborted");
            // TODO : send message to web UI
        }
    }

    private void handleIntrospectionReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();

        String status = (String) content.get("status");

        if(status == "error") return; // TODO send error to UI
        else if (status == "ok") {
            boolean found = (boolean) content.get("found");
            if(found) {
                String data = (String) content.get("data");
                // TODO send data field to UI
                return;
            }
        }
    }

    private void handleCompletionReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();

        String status = (String) content.get("status");

        if(status == "error") return; // TODO send error to UI
        else if (status == "ok") {
            // TODO send info to corresponding UI, probably sending UI username via metadata and retrieving it here
        }


    }

    private void handleHistoryReplyMessage (JupyterMessage message) {
        JSONObject content = message.getContent();
        // TODO
    }

    private void handleCodeCompletenessReplyMessage (JupyterMessage message) {
        // TODO
    }

    private void handleConnectionReplyMessage (JupyterMessage message) {
        // TODO
    }

    private void handleCommInfoReplyMessage (JupyterMessage message) {
        // TODO
    }

    private void handleKernelInfoReplyMessage (JupyterMessage message) {
        // TODO
    }

    private void handleShutdownReplyMessage (JupyterMessage message) {
        // TODO
    }
}
