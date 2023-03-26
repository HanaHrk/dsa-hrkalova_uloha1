package dsa;

import java.net.InetAddress;
import java.net.SocketAddress;

public class InputMessage {

    private final String message;
    private final int port;
    private final InetAddress address;

    private final SocketAddress socketAddress;

    public InputMessage(String message, int port, InetAddress address, SocketAddress socketAddress) {
        this.message = message;
        this.port = port;
        this.address = address;
        this.socketAddress = socketAddress;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public String getMessage() {
        return message;
    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
    }
}
