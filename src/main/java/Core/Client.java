package Core;

import JupyterMessaging.JupyterMessage;
import JupyterMessaging.ShellMessages;

import java.util.ArrayList;

/**
 * Created by antoine on 28/04/17.
 */
public class Client {

    public static ArrayList<Kernel> kernels;

    public static void main (String[] args) throws Exception {
        // Configure behavior on SIGINT
        Runtime.getRuntime().addShutdownHook(SIGINTHandler());

        kernels = new ArrayList<>();

        // Create kernel
        Kernel kernel = new Kernel();
        kernels.add(kernel);

        // Configure hb and iopub channel to log what they receive
        kernel.iopub.doLog(true);
        kernel.hb.doLog(true);

        Thread.sleep(3000);

        ShellMessages shellMessages = new ShellMessages(kernel);
        String[] executeCode = shellMessages.createExecuteRequestMessage("2+3");
        kernel.shell.send(executeCode);

        int counter = 0;
        // Send a message
        while(true) {
            if(counter == 5) {
              //  kernel.stopChannels();
            } else if (counter == 10) {
              //  kernel.startChannels();
            }
            counter++;
            Thread.sleep(2000);
        }
    }

    /**
     * Stop all running kernels on SIGINT signal
     * @return : the thread stopping the containers
     */
    private static Thread SIGINTHandler () {
        Thread handler = new Thread () {
            @Override
            public void run () {
                System.out.println("Shutting down all the kernels");

                for(int i= 0; i< Client.kernels.size(); i++) {
                    Client.kernels.get(i).stopContainer();
                }
            }
        };

        return handler;
    }
}
