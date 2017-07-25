package fr.irisa.diverse.Jupyter.JupyterChannels;

import fr.irisa.diverse.Core.Kernel;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * The Heartbeat channel is one of the five channel used to communicate with a Jupyter Kernel.
 *
 * A Heartbeat is very common in Socket communication. It sends a short message, every second, for instance "ping",
 * to the connected server and wait for its answer.
 *
 * We use it in order to know if we are still connected to the server.
 *
 * TODO : add disconnection monitoring.
 *
 * Messaging in Jupyter documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html
 * Heartbeat documentation : http://jupyter-client.readthedocs.io/en/latest/messaging.html#heartbeat-for-kernels
 *
 * EXTENDS JupyterChannel : the abstract class implementing the default behavior of a Jupyter channel.
 *
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
                // Send 'ping' every second
                Thread.sleep(1000);
                socket.send("ping".getBytes(), 0);

                // Wait for the answer
                String message = socket.recvStr();
                // Log the answer if configured
                if (this.log) System.out.println("Received : " + message + " on socket " + name);

            } catch (ZMQException e) {
                // Catch a ZMQException in order to close the channel properly.
                // It is thrown only when we call context.term
                if (e.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                    break;
                }
            } catch (InterruptedException e)  {
                break;
            }

        }

        // Finally close the socket properly
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
