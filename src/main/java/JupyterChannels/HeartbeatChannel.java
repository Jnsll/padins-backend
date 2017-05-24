package JupyterChannels;

import Core.Kernel;
import org.zeromq.ZMQ;

/**
 * Created by antoine on 03/05/17.
 */
public class HeartbeatChannel extends JupyterChannel {

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

    public HeartbeatChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.REQ, kernel);
    }

    /**
     * Run methods from Runnable interface
     */
    @Override
    public void run() {
        initializeThread();

        // Loop that will run whenever the Thread runs
        // This is where we will handle the socket behavior
        try {
            while(!Thread.currentThread().isInterrupted()) {
                // Send 'ping'
                Thread.sleep(1000);

                socket.send("ping".getBytes(), 0);

                String message = socket.recvStr();
                if(this.log) System.out.println("Received : " + new String(message) + " on socket " + name);

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        stopThread();

    }

    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM METHODS
       =================================================================================================================
       ===============================================================================================================*/

    @Override
    protected void initializeThread() {
        // First : connect to the ZMQ server
        this.socket.connect(this.socketAddress);
        this.connected = true;

        System.out.println("Connected to HB on " + socketAddress);
    }

    @Override
    protected void stopThread() {
        // When stopping the thread : destroy the context & not connected anymore
        this.socket.close();
        this.context.term();
        this.connected = false;
    }

}
