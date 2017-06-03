package fr.irisa.diverse.JupyterMessaging;

import fr.irisa.diverse.Core.Kernel;
import fr.irisa.diverse.JupyterChannels.IOPubChannel;
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

        Long executionCount = (Long) content.get("execution_count");
        if(executionCount > kernel.getNbExecutions()) kernel.setNbExecutions(executionCount);
    }

    private void handleExecuteResultMessage(JupyterMessage message) {
        JSONObject content = message.getContent();

        Long executionCount = (Long) content.get("execution_count");
        String status = (String) content.get("status");
        if(executionCount > kernel.getNbExecutions()) {
            // Increase the executionCount of the kernel
            kernel.setNbExecutions(executionCount);

            // Handle the status field
            switch (status) {
                case "ok" :
                    // Send the result to the kernel
                    JSONObject result = (JSONObject) content.get("payload");
                    kernel.handleExecutionResult(result, executionCount);
                    break;
                case "error" :
                    kernel.owningWorkspace.errorFromKernel("Execution_reply status : error");
                    break;
                case "abort" :
                    kernel.owningWorkspace.errorFromKernel("Execution aborted");
            }

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
        System.out.println(stream + " : " + text);
    }


}
