package fr.irisa.diverse.Demos;

import fr.irisa.diverse.Core.Workspace;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPMessage;
import fr.irisa.diverse.MessageHandlers.FBPNetworkProtocol.FBPNetworkProtocolManager;

import java.util.ArrayList;

/**
 * Created by antoine on 01/06/17.
 */
public class Demo {

    private static ArrayList<Workspace> workspaces;

    public static void main (String[] args) throws Exception {
        init();

        System.out.println("STEP 1 : Creating workspace");
        Workspace workspace = new Workspace("Hillslope1D", null);
        workspaces.add(workspace);
        FBPNetworkProtocolManager manager = new FBPNetworkProtocolManager(workspace);

        System.out.println("\nSTEP 2 : Display workspace's flow");
        System.out.println(workspace.getFlow().serialize());

        System.out.println("\nSTEP 3 : Creating a Raw data node and a Model node");
        FBPMessage createNode1 = new FBPMessage("graph", "addnode", "{\"id\":\"123456789\", \"component\":\"Raw data\", \"metadata\":\"{}\", \"graph\":\""  + workspace.getUuid() + "\"}");
        FBPMessage createNode2 = new FBPMessage("graph", "addnode", "{\"id\":\"931261982\", \"component\":\"Model\", \"metadata\":\"{}\", \"graph\":\""  + workspace.getUuid() + "\"}");
        manager.onMessage(createNode1.toJSONString());
        manager.onMessage(createNode2.toJSONString());

        System.out.println("\nSTEP 4 : Connecting those 2 nodes with an edge");
        FBPMessage connectNode1And2WithEdge = new FBPMessage("graph", "addedge", "{\"src\":{\"node\":\"123456789\", \"port\":\"raw-data\"}, \"tgt\":{\"node\":\"931261982\", \"port\":\"Pre-processed data\"},\"metadata\":{}, \"graph\":\""  + workspace.getUuid() + "\"}");
        manager.onMessage(connectNode1And2WithEdge.toJSONString());

        System.out.println("\nSTEP 5 : Display workspace's flow after Step 4");
        System.out.println(workspace.getFlow().serialize());

        System.out.println("\nSTEP 6 : Run the flow");
        FBPMessage startFlow = new FBPMessage("network", "start", "{\"graph\":\"" + workspace.getUuid() + "\"}");
        manager.onMessage(startFlow.toJSONString());

        /*System.out.println("\nSTEP 6 : Create a Processing node which contains a code 8+5");
        FBPMessage createNode3 = new FBPMessage("graph", "addnode", "{\"id\":\"819846731\", \"component\":\"Processing\", \"metadata\":{\"code\":\"8+5\"}, \"graph\":\""  + workspace.getUuid() + "\"}");
        manager.onMessage(createNode3.toJSONString());

        Thread.sleep(1000);

        System.out.println("\nSTEP 7 : Run Processing block's code");
        Node processing = workspace.getFlow().getNode("819846731", workspace.getUuid());
        JSONObject metadata = processing.getMetadata();
        String code = (String) metadata.get("code");
        Kernel k = workspace.getKernel("819846731");
        k.iopub.doLog(true);
        k.shell.doLog(true);
        k.executeCode(code);
*/
        System.out.println("FINISHED DEMO");

        int i = 0;
        while(i<=5) {
            Thread.sleep(1000);
            i++;
        }
    }










    private static void init () {
        // Configure behavior on SIGINT
        Runtime.getRuntime().addShutdownHook(SIGINTHandler());

        workspaces = new ArrayList<>();
    }

    /**
     * Stop all running kernels on SIGINT signal
     * @return : the thread stopping the containers
     */
    private static Thread SIGINTHandler () {

        return new Thread () {
            @Override
            public void run () {
                System.out.println("Shutting down all the kernels");

                for(int i= 0; i< workspaces.size(); i++) {
                    System.out.println("Shutting down kernel for workspace" + i);
                    workspaces.get(i).stopKernels();
                }
            }
        };
    }
}
