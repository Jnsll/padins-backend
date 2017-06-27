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
 * Created by antoine on 23/06/17.
 */
public class FileExplorerMessageHandler implements MessageHandler.Whole<FBPMessage> {

    // Attributes
    private ServerSocket owningSocket = null;
    Workspace owningWorkspace = null;
    public final String PROTOCOL_NAME = "fileexplorer";

    // Constructor
    public FileExplorerMessageHandler (Workspace workspace) {
        this.owningWorkspace = workspace;
    }

    /* =================================================================================================================
                                                  GETTERS AND SETTERS
       ===============================================================================================================*/

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
