package Utils;

/**
 * Created by antoine on 25/05/2017.
 */
public class Socket extends java.net.Socket {

    public String getId () {
        return getLocalAddress().toString() + getLocalPort() + getInetAddress().toString() + getPort();
    }
}
