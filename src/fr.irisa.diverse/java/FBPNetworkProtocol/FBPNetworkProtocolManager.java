package FBPNetworkProtocol;

import Core.Workspace;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by antoine on 26/05/2017.
 */
public class FBPNetworkProtocolManager implements MessageHandler.Whole<String> {

    // TODO : implement capabilities as described here : https://flowbased.github.io/fbp-protocol/#capabilities

    // Attributes
    private NetworkMessageHandler network = null;
    private GraphMessageHandler graph = null;
    private ComponentMessageHandler component = null;
    private RuntimeMessageHandler runtime = null;
    private TraceMessageHandler trace = null;
    private Session owningSession = null;
    private Workspace owningWorkspace = null;

    private String componentsLibrary = "";

    // Constructor
    public FBPNetworkProtocolManager (Workspace workspace) {
        this.owningWorkspace = workspace;

        network = new NetworkMessageHandler(this);
        graph = new GraphMessageHandler(this);
        component = new ComponentMessageHandler(this);
        runtime = new RuntimeMessageHandler(this);
        trace = new TraceMessageHandler(this);

        componentsLibrary = workspace.getLibrary();

    }

    /* =================================================================================================================
                                                  GETTERS AND SETTERS
       ===============================================================================================================*/

    public void setSession (Session session) {
        owningSession = session;
    }

    public String getComponentsLibrary() {
        return componentsLibrary;
    }

    /* =================================================================================================================
                                       MessageHandler.Whole INTERFACE METHOD IMPLEMENTATION
           ===============================================================================================================*/
    @Override
    public void onMessage(String msg) {
        // Parse the received message add put it into a JSONObject.
        FBPMessage message = new FBPMessage(msg);

        String protocol = message.getProtocol();

        // Redirect message to proper handler
        switch (protocol) {
            case "runtime" :
                runtime.handleMessage(message);
                break;
            case "graph" :
                graph.handleMessage(message);
                break;
            case "component" :
                component.handleMessage(message);
                break;
            case "network" :
                network.handleMessage(message);
                break;
            case "trace" :
                trace.handleMessage(message);
                break;
            default :
                System.err.println("Received message for unknown protocol : " + message.toJSONString());
                break;
        }


    }

    /* =================================================================================================================
                                                  PUBLIC METHODS
       ===============================================================================================================*/

    public Session getOwningSession() {
        return owningSession;
    }

    public void send (String msg) {
        try {
            owningSession.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll (String msg) {
        Map<String, Session> clients = owningWorkspace.getConnectedClients();

        Set<String> keys = clients.keySet();
        Iterator iterator = keys.iterator();
        while(iterator.hasNext()) {
            Session client = clients.get(iterator.next());
            try {
                client.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
