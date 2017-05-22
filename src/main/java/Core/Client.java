package Core;

import JupyterMessaging.Manager;
import JupyterMessaging.ShellMessaging;

import java.util.ArrayList;

/**
 * Created by antoine on 28/04/17.
 */
public class Client {

    public static ArrayList<Kernel> kernels;

    public static void main (String[] args) throws Exception {

        init();

        // Create kernel
        Kernel kernel = new Kernel();
        kernels.add(kernel);

        // Configure hb and iopub channel to log what they receive
        kernel.iopub.doLog(true);
        kernel.shell.doLog(true);
        //kernel.hb.doLog(true);

        // Wait for the kernel to start
//        while (kernel.isBusy()){
//            Thread.sleep(100);
//        }

        Thread.sleep(3000);
        System.out.println("Send execution request for code : 2+3");
        Manager messagesManager = kernel.getMessagesManager();

        String message = messagesManager.sendMessageOnShell().sendExecuteRequestMessage("2+3");

        int counter = 0;
        // Send a message
        while(true) {
            if(counter == 5) {
              //  kernel.stopChannels();
            } else if (counter == 10) {
              //  kernel.startChannels();
            }
            counter++;
            kernel.verifyChannelsAreOk();
            Thread.sleep(2000);
        }
    }

    private static void init () {
        // Configure behavior on SIGINT
        Runtime.getRuntime().addShutdownHook(SIGINTHandler());

        kernels = new ArrayList<>();
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
                    Client.kernels.get(i).stop();
                }
            }
        };

        return handler;
    }
}
