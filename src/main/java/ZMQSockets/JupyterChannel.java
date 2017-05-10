package ZMQSockets;

import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ;
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

    public JupyterChannel(String name, String transport, String ip, long port, String containerID, int socketType) {
        // Store the name, identity & type
        this.name = name;
        this.identity = containerID;
        this.socketType = socketType;

        // Create the ZMQ context and the socket (without connecting it)
        this.context = ZMQ.context(1);
        this.socket = context.socket(socketType);
        // Set Linger to 1s to prevent hangs at exit
        this.socket.setLinger(1000);

        // Store the address of the socket
        this.socketAddress = transport + "://" + ip + ":" + port;
        socket.setIdentity(containerID.getBytes());
    }

    /**
     * If true, the channel will log every message it receives. Otherwise, doesn't log anything.
     * @param log : boolean
     */
    public void doLog (boolean log) {
        this.log = log;
    }

    /**
     * Stop the channel and disconnect it
     */
    public void stop () {
        // TODO
    }

    /**
     * Resume the channel and reconnect it
     */
    public void resume() {
        // TODO
    }

    public boolean isRunning() {
        // TODO
        return true;
    }

    /* Information from the documentation
       Example of a correct message

       {'header': {'version': '5.1', 'date': datetime.datetime(2017, 4, 28, 15, 27, 43, 7311, tzinfo=datetime.timezone.utc),
       'session': '61ba1939-d3e8bd81cd44b767e1bc432b', 'username': 'username', 'msg_type': 'execute_request',
       'msg_id': '48215264-3c3a1f9d8d23fe0e32b60d58'}, 'msg_id': '48215264-3c3a1f9d8d23fe0e32b60d58',
       'msg_type': 'execute_request', 'parent_header': {},
       'content': {'code': '2+3', 'silent': False, 'store_history': True, 'user_expressions': {}, 'allow_stdin': True,
       'stop_on_error': True}, 'metadata': {}}


     */
}
