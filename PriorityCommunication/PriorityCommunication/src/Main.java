import dsa.Server;

public class Main {

    private static final int MULTICAST_PORT = 9090;
    private static final int UNICAST_TIMEOUT = 400;

    public static void main(String[] args) {
        new Thread(new Server(MULTICAST_PORT, 9091, UNICAST_TIMEOUT)).start();
        new Thread(new Server(MULTICAST_PORT, 9092, UNICAST_TIMEOUT)).start();
        new Thread(new Server(MULTICAST_PORT, 9093, UNICAST_TIMEOUT)).start();
        new Thread(new Server(MULTICAST_PORT, 9094, UNICAST_TIMEOUT)).start();
    }
}
