package JupyterMessaging;

import Core.Kernel;
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
public class ShellMessages {

    private Kernel kernel = null;

    public ShellMessages (Kernel kernel) {
        this.kernel = kernel;
    }

    /* =================================================================================================================
                                               METHODS TO CREATE SHELL MESSAGES
     =================================================================================================================*/

    /**
     * Implementation of execute_request message according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#execute
     * @param code : python code to execute
     * @return : the message to send through the channel
     */
    public String[] createExecuteRequestMessage (String code) {
        JupyterMessage message = new JupyterMessage(kernel, "execute_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("silent", "False");
        content.put("store_history", "True");
        content.put("user_expressions", "");
        content.put("allow_stding", "True");
        content.put("stop_on_error", "False");

        message.setContent(content);

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection inspect_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#introspection
     */
    public String[] createIntrospectionRequestMessage (String code, int cursorPos) {
        JupyterMessage message = new JupyterMessage(kernel, "inspect_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("cursor_pos", Integer.toString(cursorPos));
        content.put("detail_level", "1");

        message.setContent(content);

        return message.getMessageToSend();
    }

    /**
     * Implementation of introspection complete_request according to documentation
     * http://jupyter-client.readthedocs.io/en/latest/messaging.html#completion
     */
    public String[] createCompletionRequestMessage (String code, int cursorPos) {
        JupyterMessage message = new JupyterMessage(kernel, "complete_request");

        JSONObject content = new JSONObject();
        content.put("code", code);
        content.put("cursor_pos", Integer.toString(cursorPos));

        message.setContent(content);

        return message.getMessageToSend();
    }
}
