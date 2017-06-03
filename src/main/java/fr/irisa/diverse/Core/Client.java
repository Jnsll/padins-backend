package fr.irisa.diverse.Core;

import fr.irisa.diverse.JupyterMessaging.Manager;

import java.util.ArrayList;

/**
 * Created by antoine on 28/04/17.
 */
class Client {

    private static ArrayList<Kernel> kernels;

    public static void main (String[] args) throws Exception {

        init();

        // Create kernel
        /*Kernel kernel = new Kernel();
        kernels.add(kernel);

        // Configure hb and iopub channel to log what they receive
        //kernel.iopub.doLog(true);
        //kernel.shell.doLog(true);

        Thread.sleep(3000);
        Manager messagesManager = kernel.getMessagesManager();

        messagesManager.sendMessageOnShell().sendKernelInfoRequestMessage();

        // Wait for the kernel to start
        while (kernel.isBusy()){
            Thread.sleep(100);
        }

        Thread.sleep(2000);

        messagesManager.sendMessageOnShell().sendExecuteRequestMessage("2+3");

        int counter = 0;
        // Send a message
        while(true) {
            if(counter == 5) {
                messagesManager.sendMessageOnShell().sendExecuteRequestMessage("8+12");
            } else if (counter == 10) {
                messagesManager.sendMessageOnShell().sendExecuteRequestMessage("2+3");
            }
            counter++;
            kernel.verifyChannelsAreOk();
            Thread.sleep(2000);
        }*/
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

        return new Thread () {
            @Override
            public void run () {
                System.out.println("Shutting down all the kernels");

                for(int i= 0; i< Client.kernels.size(); i++) {
                    Client.kernels.get(i).stop();
                }
            }
        };
    }
}
