package fr.irisa.diverse.FBPNetworkProtocol;

import fr.irisa.diverse.Core.Workspace;
import org.json.simple.JSONObject;
import fr.irisa.diverse.Flow.*;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.io.IOException;
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
    Workspace owningWorkspace = null;
    final String FBP_NETWORK_PROTOCOL_VERSION = "0.6";
    private Flow flow = null;

    private String componentsLibrary = "";

    // Constructor
    public FBPNetworkProtocolManager (Workspace workspace) {
        this.owningWorkspace = workspace;
        componentsLibrary = workspace.getLibrary();

        network = new NetworkMessageHandler(this);
        graph = new GraphMessageHandler(this);
        component = new ComponentMessageHandler(this);
        runtime = new RuntimeMessageHandler(this);
        trace = new TraceMessageHandler(this);

        flow = workspace.getFlow();

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

    public void send (FBPMessage msg) {
        // TODO : add secret handling
        try {
            owningSession.getBasicRemote().sendText(msg.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll (FBPMessage msg) {
        Map<String, Session> clients = owningWorkspace.getConnectedClients();

        Set<String> keys = clients.keySet();
        for (Object key : keys) {
            Session client = clients.get(key);
            try {
                // TODO : add secret handling for each client
                client.getBasicRemote().sendText(msg.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendError(String protocol, String error) {
        JSONObject obj = new JSONObject();
        obj.put("message", error);
        String payload = obj.toJSONString();

        FBPMessage msg = new FBPMessage(protocol, "error", payload);

        send(msg);
    }
}
