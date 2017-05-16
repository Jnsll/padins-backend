package JupyterChannels;

import Core.Kernel;
import org.zeromq.ZMQ;

/**
 * Created by antoine on 03/05/17.
 */
public class StdinChannel extends JupyterChannel {

    /* Superclass attributes
        String name;
        ZContext context = null;
        Socket socket = null;
        String socketAddress;
        String identity;
        boolean connected = false;
        boolean log = false;
        Kernel owningKernel;
        Manager messagesManager;
        Thread thread;
     */

    public StdinChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER, kernel);
    }

    /**
     * Run methods from Runnable interface
     */
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //TODO : adapt
        // First : connect the server
//        this.socket.connect(this.socketAddress);
//        this.connected = true;
//
//        // Loop that will run whenever the Thread runs
//        // This is where we will handle the socket behavior
//        while(!Thread.currentThread().isInterrupted()) {
//
//            System.out.println("Thread " + this.name + "running");
//
//            // TODO : implement a Poller !!! See : http://learning-0mq-with-pyzmq.readthedocs.io/en/latest/pyzmq/multisocket/zmqpoller.html
//            byte[] reply = socket.recv(0);
//            System.out.println("Thread " + this.name + "running - step 2");
//            System.out.println("Received : " + new String(reply));
//        }
//        System.out.println("Thread " + this.name + "after WHILE");
//        // When stopping the thread : destroy the context & not connected anymore
//        this.socket.disconnect(socketAddress);
//        this.context.term();
//        this.connected = false;
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM METHODS
       =================================================================================================================
       ===============================================================================================================*/

    public void send (String message) {

        // Do nothing : must be implemented in a subclass
    }
}
