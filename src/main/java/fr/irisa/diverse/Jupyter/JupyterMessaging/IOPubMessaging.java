package fr.irisa.diverse.Jupyter.JupyterMessaging;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.Jupyter.JupyterChannels.IOPubChannel;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * IOPub Messaging handle messages on the IOPub channel, according to the documentation of Messaging in Jupyter.
 *
 * Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html
 * IOPub channel documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#messages-on-the-iopub-pub-sub-channel
 *
 * Created by antoine on 10/05/2017.
 */
class IOPubMessaging {

    /* =================================================================================================================
                                                    ATTRIBUTES
     =================================================================================================================*/
    private Kernel kernel = null;
    private IOPubChannel channel = null;

    /* =================================================================================================================
                                                    CONSTRUCTOR
     =================================================================================================================*/
    public IOPubMessaging(Kernel kernel, IOPubChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
    }

    /**
     * Handle the given message, coming from the IOPub Channel
     * @param type {String} the type of the message
     * @param message {JupyterMessage} the message itself
     */
    public void handleMessage (String type, JupyterMessage message) {
        switch (type) {
            case "status" :
                handleStatusMessage(message);
                break;
            case "execute_result" :
                handleExecuteResultMessage (message);
                break;
            case "code_input" :
                handleCodeInputMessage (message);
                break;
            case "display_data" :
                handleDisplayDataMessage (message);
                break;
            case "error" :
                handleErrorMessage (message);
                break;
            case "update_display_data" :
                handleUpdateDisplayDataMessage (message);
                break;
            case "stream" :
                handleStreamMessage (message);
                break;
        }
    }


    /* =================================================================================================================
                                         METHODS TO HANDLE INCOMING IOPUB MESSAGES
     =================================================================================================================*/

    /**
     * Handle a status message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#kernel-status
     *
     * Our implementation behavior: store the status in the Kernel object.
     * @param message {JupyterMessage} the received message
     */
    private void handleStatusMessage (JupyterMessage message) {
        // Retrieve the new state
        String executionState = (String) message.getContent().get("execution_state");

        // Set the state of the kernel
        if(executionState.equals("idle")) kernel.setIdleState(true);
        else kernel.setIdleState(false);
    }

    /**
     * Handle a error message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#execution-errors
     *
     * Our implementation behavior: retrieve the Traceback and transmit it to the connected UIs
     * @param message {JupyterMessage} the received message
     */
    private void handleErrorMessage(JupyterMessage message) {
        JSONObject content = message.getContent();

        // Content contains ename, evalue and traceback
        kernel.owningWorkspace.clientCommunicationManager.handleTracebackFromKernel((JSONArray) content.get("traceback"), this.kernel);
    }

    /**
     * Handle an execute_result message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#id6
     *
     * Our implementation behavior: store the execution count and log the result
     * @param message {JupyterMessage} the received message
     */
    private void handleExecuteResultMessage(JupyterMessage message) {
        JSONObject content = message.getContent();

        Long executionCount = (Long) content.get("execution_count");
        if(executionCount >= kernel.getNbExecutions()) {
            // Increase the executionCount of the kernel
            kernel.setNbExecutions(executionCount);

            // Send the result to the kernel
            JSONObject result = (JSONObject) content.get("data");
            System.out.println("Received execution result for execution " + executionCount + " : " + result.toJSONString());

        }
    }

    /**
     * Handle an execute_input message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#code-inputs
     *
     * Our implementation behavior: do nothing
     * @param message {JupyterMessage} the received message
     */
    private void handleCodeInputMessage(JupyterMessage message) {
        // TODO : redirect the content to every UI
    }

    /**
     * Handle a display_data message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#display-data
     *
     * Our implementation behavior: log the data. No need to send it to UIs because there is a
     * much more powerful component on the frontend to display data.
     * @param message {JupyterMessage} the received message
     */
    private void handleDisplayDataMessage(JupyterMessage message) {
        JSONObject content = message.getContent();

        // TEMPORARY
        String data = content.get("data").toString();
        String metadata = content.get("metadata").toString();
        System.out.println("Received Display_data message with data : \n" + data + "\nAnd metadata : \n" + metadata);
    }

    /**
     * Handle an update_display_data message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#update-display-data
     *
     * Our implementation behavior: do nothing. No need to send it to UIs because there is a
     * much more powerful component on the frontend to display data.
     * @param message {JupyterMessage} the received message
     */
    private void handleUpdateDisplayDataMessage(JupyterMessage message) {
        // TODO
    }

    /**
     * Handle a stream message, according to this doc :
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#streams-stdout-stderr-etc
     *
     * Our implementation behavior:
     * - STDOUT : we use the stdout to retrieve the data to share across nodes.
     * So, we store the lines of the stdout in a variable. Then the kernel handles it by storing
     * the data into the corresponding node.
     *
     * -STDERR : we do not handle the stderr for now. The error message sends the traceback use for
     * debugging.
     *
     * @param message {JupyterMessage} the received message
     */
    private void handleStreamMessage(JupyterMessage message) {
        JSONObject content = message.getContent();
        String stream = content.get("name").toString();
        String text = content.get("text").toString();
        // System.out.println(stream + " : " + text);

        if (message.getParentHeader().get("msg_type").equals("execute_request")) {
            if (content.get("name").equals("stdout")) {
                // If we receive something on stdout, we store the result of each variable
                String result = content.get("text").toString();
                this.kernel.handleExecutionResult(result.split("\\r\\n|\\n|\\r"));

            } else if (content.get("name").equals("stderr")) {

            }
        }

    }


}
