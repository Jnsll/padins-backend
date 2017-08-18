package fr.irisa.diverse.MessageHandlers.FileExplorer;

import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPMessage;
import fr.irisa.diverse.Webserver.Servlets.WebsocketOthers.ServerSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.websocket.MessageHandler;
import java.io.File;
import java.util.UUID;

/**
 * The FileExplorerMessageHandler implements a messaging protocol that provides a file-explorer service.
 * Each workspace has its own directory and cannot access other workspaces' directories.
 *
 * The service provide one endpoint :
 * - getnodes : returns the file structure of the workspace's, from its root directory, as a tree.
 *
 * In order to keep consistent the format of messages exchanged between the frontend and the backend,
 * we stuck with using FBP-formatted messages.They are implemented in
 * fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPMessage
 *
 * A file upload service is implemented as a REST full API in fr.irisa.diverse.Webserver.Servlets.UploadServlet
 *
 * INTERFACES IMPLEMENTATIONS
 * The class implements MessageHandler.Whole in order to bring formalism. It also makes it usable as the MessageHandler
 * of a socket.
 *
 * Created by antoine on 23/06/17.
 */
@SuppressWarnings("unchecked")
public class FileExplorerMessageHandler implements MessageHandler.Whole<FBPMessage> {

    // Attributes
    private ServerSocket owningSocket = null;
    private Workspace owningWorkspace = null;
    private final String PROTOCOL_NAME = "fileexplorer";

    /* =================================================================================================================
                                                    CONSTRUCTOR
       ===============================================================================================================*/

    public FileExplorerMessageHandler (Workspace workspace) {
        this.owningWorkspace = workspace;
    }

    /* =================================================================================================================
                                                  GETTERS AND SETTERS
       ===============================================================================================================*/

    /**
     * Set the client's socket instance.
     *
     * @param socket {ServerSocket} the client socket
     */
    public void setSocket (ServerSocket socket) {
        owningSocket = socket;
    }

    /* =================================================================================================================
                                       MessageHandler.Whole INTERFACE METHOD IMPLEMENTATION
       ===============================================================================================================*/

    @Override
    public void onMessage(FBPMessage message) {

        String command = message.getCommand();

        // Redirect message to proper handler
        switch (command) {
            // TODO
            case "getnodes":
                sendNodes();
                break;
            default :
                System.err.println("Received message for file-explorer protocol : " + message.toJSONString());
                break;
        }


    }

    /* =================================================================================================================
                                                METHODS TO SEND MESSAGES
       ===============================================================================================================*/

    /**
     * Send a "nodes" message containing the file structure of the workspace's root directory as a tree.
     */
    private void sendNodes() {
        JSONArray structure = rootFolderStructure();

        JSONObject payload = new JSONObject();
        payload.put("nodes", structure);

        FBPMessage msg = new FBPMessage(PROTOCOL_NAME, "updatenodes", payload.toJSONString());
        this.owningSocket.send(msg.toJSONString());
    }

    /* =================================================================================================================
                                 METHODS RELATED TO FOLDER MANAGEMENT (DELETE, ADD, TRAVERSE, ETC.)
       ===============================================================================================================*/

    /**
     * Returns the root directory's file structure of a workspace as a tree.
     * It goes through all the files and subdirectories so all files are listed in the tree.
     *
     * A folder content is structure as follow :
     * Tree : {
     *   name: String,
     *   id: String,
     *   children : Array<Tree>,
     *   isExpanded: Boolean
     * }
     *
     * @return {JSONArray} the file structure as a tree, respecting the format described above.
     */
    private JSONArray rootFolderStructure () {
        String rootPath = owningWorkspace.getPathToWorkspaceFolder().toString();

        JSONArray res = new JSONArray();
        JSONObject rootInfo = new JSONObject();
        rootInfo.put("name", owningWorkspace.getName());
        rootInfo.put("id", UUID.randomUUID().toString());
        rootInfo.put("children", folderStructure(rootPath));
        rootInfo.put("isExpanded", true);

        res.add(rootInfo);

        return res;
    }

    /**
     * Returns the file structure of the given path as a tree. It includes all its subdirectories structure.
     *
     * A folder content is structure as follow :
     * Tree : {
     *   name: String,
     *   id: String,
     *   children : Array<Tree>,
     *   isExpanded: Boolean
     * }
     *
     * @param path {String} absolute path to the directory
     * @return {JSONArray} the content of the directory, as a tree. Formatted as described above.
     */
    private JSONArray folderStructure (String path) {
        JSONArray res = new JSONArray();
        // First : load the list of folder that are in the given path
        File dir = new File(path);
        File[] files = dir.listFiles();

        // Go through the folder
        for (File f: files) {
            JSONObject o = new JSONObject();
            o.put("id", UUID.randomUUID().toString());
            o.put("name", f.getName());
            if (f.isDirectory() && f.listFiles().length > 0) {
                o.put("children", folderStructure(path + "/" + f.getName()));
                o.put("isExpanded", true);
            } else if (f.isDirectory()) {
                o.put("children", new JSONArray());
            }

            res.add(o);
        }

        // After having traversed the folder, return the result
        return res;

    }
}
