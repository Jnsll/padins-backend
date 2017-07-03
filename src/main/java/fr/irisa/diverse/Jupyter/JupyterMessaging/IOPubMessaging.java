package fr.irisa.diverse.Jupyter.JupyterMessaging;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.Jupyter.JupyterChannels.IOPubChannel;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by antoine on 10/05/2017.
 */
class IOPubMessaging {

    // Attributes
    private Kernel kernel = null;
    private IOPubChannel channel = null;

    // Constructor
    public IOPubMessaging(Kernel kernel, IOPubChannel channel) {

        this.kernel = kernel;
        this.channel = channel;
    }

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

    private void handleStatusMessage (JupyterMessage message) {
        String executionState = (String) message.getContent().get("execution_state");

        if(executionState.equals("idle")) kernel.setIdleState(true);
        else kernel.setIdleState(false);
    }

    private void handleErrorMessage(JupyterMessage message) {
        JSONObject content = message.getContent();

        // Content contains ename, evalue and traceback
        kernel.owningWorkspace.clientCommunicationManager.handleTracebackFromKernel((JSONArray) content.get("traceback"), this.kernel);
    }

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

    private void handleCodeInputMessage(JupyterMessage message) {
        // TODO : redirect the content to every UI
    }

    private void handleDisplayDataMessage(JupyterMessage message) {
        // TODO
        JSONObject content = message.getContent();

        // TEMPORARY
        String data = content.get("data").toString();
        String metadata = content.get("metadata").toString();
        System.out.println("Received Display_data message with data : \n" + data + "\nAnd metadata : \n" + metadata);
    }

    private void handleUpdateDisplayDataMessage(JupyterMessage message) {
        // TODO
    }

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
