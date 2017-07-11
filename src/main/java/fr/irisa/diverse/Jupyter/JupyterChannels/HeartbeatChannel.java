package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

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
        while(!Thread.currentThread().isInterrupted()) {
            try {
                // Send 'ping'
                Thread.sleep(1000);

                socket.send("ping".getBytes(), 0);

                String message = socket.recvStr();
                if (this.log) System.out.println("Received : " + message + " on socket " + name);

            } catch (ZMQException e) {
                if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                    break;
                }
            } catch (InterruptedException e)  {
                break;
            }

        }

        socket.setLinger(0);
        socket.close();
        this.connected = false;

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
    }

}
