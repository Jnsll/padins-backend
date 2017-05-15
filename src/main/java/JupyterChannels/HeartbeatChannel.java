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
     */
     private ZMQ.Poller items;

    public HeartbeatChannel(String name, String transport, String ip, long port, String containerID, Kernel kernel) {
        super(name, transport, ip, port, containerID, ZMQ.REQ, kernel);
    }

    /**
     * Run methods from Runnable interface
     */
    public void run() {
        // First : connect to the ZMQ server
        this.socket.connect(this.socketAddress);
        this.connected = true;

        System.out.println("Connected to HB on " + socketAddress);

        // Initialize poller to read message when they arrive
        items = this.context.poller(1);
        items.register(socket, ZMQ.Poller.POLLIN);

        // Loop that will run whenever the Thread runs
        // This is where we will handle the socket behavior
        while(!Thread.currentThread().isInterrupted()) {
            // Send 'ping'
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.send("ping".getBytes(), 0);

            byte[] message;
            items.poll();
            if(items.pollin(0)) {
                message = socket.recv(0);
                if(this.log) {
                    System.out.println("Received : " + new String(message) + " on socket " + name);
                }
            }

        }
        // When stopping the thread : destroy the context & not connected anymore
        this.socket.close();
        this.context.term();
        this.connected = false;
    }

    /* =================================================================================================================
       =================================================================================================================
                                                    CUSTOM METHODS
       =================================================================================================================
       ===============================================================================================================*/


}
