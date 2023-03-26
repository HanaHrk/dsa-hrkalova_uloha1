package dsa;

import java.net.InetAddress;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server implements Runnable {

    private final NodeInfo info;
    private final Queue<String> unprocessedMessages;

    private final Map<UUID, ExtendedNodeInfo> nodesInfoMap;
    private final MulticastCommunication multicast;
    private final UnicastCommunication unicast;
    private volatile ExtendedNodeInfo leaderInfo;

    public Server(int multicastPort, int unicastPort, int unicastTimeout) {
        this.nodesInfoMap = new ConcurrentHashMap<>();
        this.info = new NodeInfo();
        this.unprocessedMessages = new ConcurrentLinkedQueue<>();
        this.multicast = new MulticastCommunication(multicastPort);
        this.unicast = new UnicastCommunication(unicastPort, unicastTimeout);
    }

    @Override
    public void run() {
        // Run scheduled message processing
        new Thread(this::processMulticastMessage).start();
        new Thread(this::processUnicastMessage).start();

        System.out.println(info + " started.");
        sendHelloWorld();
        ServerUtils.waitForRandomRange(500, 5000);
        System.out.println(info + " ended waiting");
        if (this.leaderInfo == null) {
            this.info.setState(ServerState.CANDIDATE);
            sendLeaderRequest();
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                if (this.leaderInfo != null && this.leaderInfo.getState() == ServerState.LEADER) {
                    String message = "PING " + this.info.getUuid();
                    logSentMessage("MULTICAST: " + message);
                    this.multicast.sendMulticastMessage(message);
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    public void processMulticastMessage() {
        this.multicast.setMulticastSocketTimeOut(0);
        while (true) {
            String message = this.multicast.receiveMulticastMessage();

            String[] parts = message.split(" ");
            String messageCode = parts[0];
            UUID uuid = UUID.fromString(parts[1]);

            logReceivedMessage("MULTICAST: " + message);
            ExtendedNodeInfo sender = nodesInfoMap.get(uuid);

            if ("HELLO".equals(messageCode)) {
                int port = Integer.parseInt(parts[2]);
                InetAddress address;
                try {
                    address = InetAddress.getByName(parts[3]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                nodesInfoMap.put(uuid, new ExtendedNodeInfo(uuid, port, address));
            } else if ("TIMEOUT".equals(messageCode)) {
                continue;
            } else if ("PING".equals(messageCode)) {
                sender.setLastActivityBefore(0);
                this.unicast.sendUnicastMessage("PING " + uuid, sender.getAddress(), sender.getPort());
            } else if ("CANDIDATE".equals(messageCode)) {
                if (voteForLeader(sender)) {
                    this.leaderInfo = this.nodesInfoMap.get(uuid);
                    this.leaderInfo.setState(ServerState.LEADER);
                    System.out.println(info + " - voted for \"" + uuid + "\"");
                }
            }
        }
    }

    public void processUnicastMessage() {
        this.unicast.setUnicastSocketTimeOut(0);
        while (true) {
            String message = this.unicast.receiveUnicastMessage();

            String[] parts = message.split(" ");
            String messageCode = parts[0];
            UUID uuid = UUID.fromString(parts[1]);

            logReceivedMessage("UNICAST: " + message);
            ExtendedNodeInfo sender = nodesInfoMap.get(uuid);
            sender.setLastActivityBefore(0);
            if ("TIMEOUT".equals(messageCode)) {
                return;
            } else if ("VOTE".equals(messageCode)) {
                boolean voted = Boolean.parseBoolean(parts[2]);
                sender.setVoted(voted);
                long votes = nodesInfoMap.values().stream().filter(ExtendedNodeInfo::isVoted).count();
                if (votes == nodesInfoMap.size() / 2 + 1) {
                    info.setState(ServerState.LEADER);
                    System.out.println(info + " became leader.");
                }
            } else if ("PING".equals(messageCode)) {
                sender.setLastActivityBefore(0);
            }
        }
    }

    private void sendLeaderRequest() {
        String message = "CANDIDATE " + this.info.getUuid();
        this.multicast.sendMulticastMessage(message);
        logSentMessage(message);
    }

    private boolean voteForLeader(ExtendedNodeInfo nodeInfo) {
        if (this.leaderInfo != null) {
            this.unicast.sendUnicastMessage("VOTE " + this.info.getUuid() + " false", this.leaderInfo.getAddress(), this.leaderInfo.getPort());
            return false;
        } else {
            this.leaderInfo = nodeInfo;
            this.unicast.sendUnicastMessage("VOTE " + this.info.getUuid() + " true", this.leaderInfo.getAddress(), this.leaderInfo.getPort());
            return true;
        }
    }

    private void sendHelloWorld() {
        int port = this.unicast.getUnicastPort();
        String address = this.unicast.getLocalIP().getHostAddress();
        String message = "HELLO " + info.getUuid() + " " + port + " " + address;
        multicast.sendMulticastMessage(message);
        logSentMessage(message);
    }

    private void logReceivedMessage(String message) {
        System.out.println(info + " received \"" + message + "\"");
    }

    private void logSentMessage(String message) {
        System.out.println(info + " sent \"" + message + "\"");
    }


}
