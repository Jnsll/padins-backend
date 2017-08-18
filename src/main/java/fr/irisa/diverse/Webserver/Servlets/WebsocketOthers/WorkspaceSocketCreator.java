package fr.irisa.diverse.Webserver.Servlets.WebsocketOthers;

import fr.irisa.diverse.Core.Root;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import java.util.List;

/**
 * Handle the request of a user trying to connect to a workspace.
 *
 * The client implementation uses the subprotocol field to send the uuid of the workspace it wants to connect to.
 *
 * Created by antoine on 15/06/2017.
 */
public class WorkspaceSocketCreator implements WebSocketCreator {

    @Override
    public Object createWebSocket (ServletUpgradeRequest request, ServletUpgradeResponse response) {
        Root root = Root.getInstance();

        // Retrieve the workspace
        List<String> subprotocols = request.getSubProtocols();

        // Verify that the required subprotocol exist
        if (subprotocols != null && !subprotocols.isEmpty() && root.hasWorkspace(subprotocols.get(0))) {
            // Tell the UI we accept the subprotocol (the workspace)
            response.setAcceptedSubProtocol(subprotocols.get(0));

            return new ServerSocket(subprotocols.get(0));
        } else {
            return null;
        }
    }
}
