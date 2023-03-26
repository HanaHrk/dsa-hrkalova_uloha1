package dsa;

import java.net.InetAddress;
import java.util.UUID;

public class ExtendedNodeInfo extends NodeInfo {

    private int port;

    private InetAddress address;

    private int lastActivityBefore;

    private boolean voted;

    public ExtendedNodeInfo(UUID uuid, int port, InetAddress address) {
        super(uuid);
        this.port = port;
        this.address = address;
    }

    public ExtendedNodeInfo(int port, InetAddress address) {
        this(UUID.randomUUID(), port, address);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getLastActivityBefore() {
        return lastActivityBefore;
    }

    public void setLastActivityBefore(int lastActivityBefore) {
        this.lastActivityBefore = lastActivityBefore;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }
}
