package fr.irisa.diverse.Webclients;

import fr.irisa.diverse.Core.Workspace;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * Several clients can be connected to the same workspace. Each client communicate through a single instance of
 * a websocket.
 *
 * This class implements the behavior of the websocket that will be instancied each time a new client connects, and
 * used each time a client communicates.
 *
 * Created by antoine on 26/05/2017.
 */

@ServerEndpoint(value="/ws")
class ServerSocket {

    // Attributes
    private Workspace owningWorkspace = null;

    // Constructor
    public ServerSocket () {
        // No need to do anything
    }

    /* =================================================================================================================
                                                  SOCKET SERVER METHODS
       ===============================================================================================================*/

    // TODO : make sure there is no need for a handleMessage method thanks to the message handler assigned to the session.

    @OnOpen
    public void handleOpen(Session session) {
        System.out.println("[SOCKET] Opened new connexion");

        int port = session.getRequestURI().getPort();
        // TODO : workspace = webserver.getWorkspaceForPort(this.port);

        // Assign a message handler to be sure the treatment of every client of the workspace is synchronous.
        session.addMessageHandler(owningWorkspace.getMessageHandler(session));

        // Store the client on the workspace instance.
        owningWorkspace.newClientConnection(session);
    }

    @OnClose
    public void handleClose(Session session) {
        owningWorkspace.clientDeconnection(session);
    }

    @OnError
    public void handleError(Throwable t) {
        System.err.println("An error occured with a client on workspace : " + owningWorkspace.getName() + "\n" + t.getMessage() );
    }

}
