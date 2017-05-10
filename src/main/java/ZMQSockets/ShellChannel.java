package ZMQSockets;

import org.zeromq.ZMQ;

/**
 * This class is used to create both the shell and control channels
 *
 * Created by antoine on 03/05/17.
 */
public class ShellChannel extends JupyterChannel {

    /* Superclass attributes
        String name;
        ZContext context = null;
        Socket socket = null;
        String socketAddress;
        String identity;
        boolean connected = false;
     */

    public ShellChannel(String name, String transport, String ip, long port, String containerID) {
        super(name, transport, ip, port, containerID, ZMQ.DEALER);
    }

    /**
     * Run methods from Runnable interface
     */
    public void run() {
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
                                                    JupyterChannelInterface METHODS
       =================================================================================================================
       ===============================================================================================================*/

    public void send (String message) {

        if(this.connected) {
            if (name == "shell") {
                // TODO
                String test = "{'header': {'version': '5.1', 'date': datetime.datetime(2017, 4, 28, 15, 32, 46, 190878, tzinfo=datetime.timezone.utc), 'session': 'f9beb76b-c8f3f4b9b9346ab34de08791', 'username': 'username', 'msg_type': 'execute_request', 'msg_id': '8a422d87-c76e916c5386858181f8a1c5'}, 'msg_id': '8a422d87-c76e916c5386858181f8a1c5', 'msg_type': 'execute_request', 'parent_header': {}, 'content': {'code': '2+3', 'silent': False, 'store_history': True, 'user_expressions': {}, 'allow_stdin': True, 'stop_on_error': True}, 'metadata': {}}";
                socket.send(test.getBytes(), 0);
                System.out.println("Sent msg " + test + " from thread " + name);
            } else {
                socket.send(message.getBytes(), 0);
            }
        } else {
            System.err.println("[ERROR] " + this.name +" socket is not connected");
        }
    }
}
