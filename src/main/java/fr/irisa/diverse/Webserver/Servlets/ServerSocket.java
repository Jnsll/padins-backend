package fr.irisa.diverse.Webserver.Servlets;

import fr.irisa.diverse.Core.Root;
import fr.irisa.diverse.Core.Workspace;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.List;

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

    // Constructor
    public ServerSocket () {
        System.out.println("Socket init");
        root = Root.getInstance();
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

        // Retrieve the workspace
        List<String> subprotocols = session.getUpgradeRequest().getSubProtocols();

        // Verify that the requires subprotocol exist
        if (subprotocols.size() > 0 && root.hasWorkspace(subprotocols.get(0))) {
            // Tell the UI we accept the subprotocol (the workspace)
            session.getUpgradeResponse().setAcceptedSubProtocol(subprotocols.get(0));

            // Store the workspace instance
            owningWorkspace = root.getWorkspace(subprotocols.get(0));

            // Store the client on the workspace instance.
            owningWorkspace.newClientConnection(session);
        }
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
        owningWorkspace.getClientCommunicationManager().onMessage(message);
    }


    @OnWebSocketClose
    public void onClose(int statusCode, String reason)
    {
        if (owningWorkspace != null) owningWorkspace.clientDeconnection(session);
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

}
