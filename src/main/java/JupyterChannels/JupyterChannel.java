package JupyterChannels;

import Core.Kernel;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;


/**
 * Created by antoine on 28/04/17.
 */
public abstract class JupyterChannel implements Runnable {

    protected String name;
    protected Context context = null;
    protected Socket socket = null;
    protected String socketAddress;
    protected String identity;
    protected int socketType;
    protected boolean connected = false;
    protected boolean log = false;
    protected Kernel owningKernel;
    protected Thread thread;

    public JupyterChannel(String name, String transport, String ip, long port, String containerID, int socketType, Kernel kernel) {
        // Store the name, identity & type
        this.name = name;
        this.identity = containerID;
        this.socketType = socketType;
        this.owningKernel = kernel;

        // Create the ZMQ context and the socket (without connecting it)
        this.context = ZMQ.context(1);
        this.socket = context.socket(socketType);
        // Set Linger to 1s to prevent hangs at exit
        this.socket.setLinger(1000);

        // Store the address of the socket
        this.socketAddress = transport + "://" + ip + ":" + port;
        socket.setIdentity(containerID.getBytes());
    }

    /*==================================================================================================================
                                               THREAD RELATED METHODS
     =================================================================================================================*/


    /**
     * Start the thread that makes the channel working
     */
    public void start() {
        if(thread == null) thread = new Thread(this);

        thread.start();
    }

    /**
     * Interrupt the channel by interrupting the thread
     */
    public void stop() throws InterruptedException {
        if(thread != null && !thread.isInterrupted()) thread.interrupt();

        while(!thread.isInterrupted()) {
            Thread.sleep(100);
        }
    }

    /**
     * Resume the channel and reconnect it
     */
    public void resume() {
        if(thread != null) thread.run();
    }

    /*==================================================================================================================
                                               CUSTOM METHODS
     =================================================================================================================*/

    /**
     * If true, the channel will log every message it receives. Otherwise, doesn't log anything.
     * @param log : boolean
     */
    public void doLog (boolean log) {
        this.log = log;
    }


    public boolean isRunning() {
        return thread.isAlive();
    }

}
