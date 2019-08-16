
package others;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class provides two static methods that you are requied to
 * use with any Socket your network client program creates.  A slightly
 * different implementation of this class may be used when grading.
 */
public final class NetMonitor {

    /**
     * Private constructor, to prevent instances of this from being
     * created.
     */
    private NetMonitor() {
    }


    /**
     * Return an input stream for a socket.  This behaves the same as
     * the method Socket.getInputStream().  Call this static method
     * instead for any socket you use.
     * <p>
     * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/Socket.html#getInputStream()
     */
    public static InputStream getInputStream(Socket socket) throws IOException {
        return socket.getInputStream();
    }

    /**
     * Get an output stream for a socket.  This behaves the same as
     * the method Socket.getOutputStream().  Call this static method
     * instead for any socket you use.
     * <p>
     * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/net/Socket.html#getOutputStream()
     */
    public static OutputStream getOutputStream(Socket socket) 
            throws IOException {
        return socket.getOutputStream();
    }
}
