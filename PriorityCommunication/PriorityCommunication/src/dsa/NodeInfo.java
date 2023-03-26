package dsa;

import java.util.UUID;

public class NodeInfo {
    private final UUID uuid;

    private ServerState state;

    private int votes;

    public NodeInfo(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID for dsa.NodeInfo must not be null.");
        }
        this.uuid = uuid;
        state = ServerState.FOLLOWER;
    }

    public NodeInfo() {
        this(UUID.randomUUID());
    }

    public UUID getUuid() {
        return uuid;
    }

    public ServerState getState() {
        return state;
    }

    public void setState(ServerState state) {
        this.state = state;
    }

    public int getVotes() {
        return votes;
    }

    public int addVotes() {
        votes++;
        return votes;
    }

    @Override
    public String toString() {
        return "Server \"" + uuid + "\"";
    }
}
