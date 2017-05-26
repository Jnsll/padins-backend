package FBPNetworkProtocol;

import javax.websocket.MessageHandler;

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

    // Constructor
    public FBPNetworkProtocolManager () {

        network = new NetworkMessageHandler(this);
        graph = new GraphMessageHandler(this);
        component = new ComponentMessageHandler(this);
        runtime = new RuntimeMessageHandler(this);
        trace = new TraceMessageHandler(this);

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
                                                  PRIVATE METHODS
       ===============================================================================================================*/
}
