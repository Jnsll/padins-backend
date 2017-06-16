package fr.irisa.diverse.Webserver.Servlets.WebsocketOthers;

import fr.irisa.diverse.Core.Root;
import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.FBPNetworkProtocol.FBPNetworkProtocolManager;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Several clients can be connected to the same workspace. Each client communicate through a single instance of
 * a websocket (this class).
 *
 * This class implements the behavior of the websocket that will be instantiated each time a new client connects, and
 * used each time a client communicates.
 *
 * Created by antoine on 26/05/2017.
 */

@WebSocket
public class ServerSocket {

    // Attributes
    private Workspace owningWorkspace = null;
    private Session session;
    private Root root;
    private FBPNetworkProtocolManager communicationManager = null;
    private String workspaceId;

    // Constructor
    public ServerSocket (String subprotocol) {
        root = Root.getInstance();
        workspaceId = subprotocol;
        communicationManager = new FBPNetworkProtocolManager(root.getWorkspace(workspaceId));
        // No need to do anything
    }

    /* =================================================================================================================
                                                  SOCKET SERVER METHODS
       ===============================================================================================================*/

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("[SOCKET] Opened new connexion");
        this.session = session;
        communicationManager.setSocket(this);

        // Store the workspace instance
        owningWorkspace = root.getWorkspace(workspaceId);

        // Send the flow to the newly connected client
        JSONObject flow = new JSONObject();
        flow.put("protocol", "flow");
        flow.put("flow", owningWorkspace.getFlow().getFlowObject());
        send(flow.toJSONString());

        // Store the client on the workspace instance.
        owningWorkspace.newClientConnection(this);

        System.out.println("Socket connected on workspace : " + workspaceId);
    }


    @OnWebSocketMessage
    public void onText(String message)
    {
        if (session == null || owningWorkspace == null)
        {
            // no connection, do nothing.
            // this is possible due to async behavior
            return;
        }

        // Redirect the message to the Message Handler
        communicationManager.onMessage(message);
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        if (owningWorkspace != null) owningWorkspace.clientDeconnection(this);
        this.session = null;
    }


    @OnWebSocketError
    public void onError(Throwable t) {
        if (owningWorkspace != null) {
            System.err.println("An error occurred with a client on workspace : " + owningWorkspace.getName() + "\n" + t.getMessage() );
        } else {
            System.err.println("An error occurred with a client : " + t.getMessage() );
        }

    }

    public boolean send (String msg) {
        if (this.session != null) {
            try {
                session.getRemote().sendString(msg);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

}
