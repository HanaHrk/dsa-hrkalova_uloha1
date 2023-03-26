package dsa;

import java.io.IOException;
import java.net.*;

public class MulticastCommunication {

    private int multicastPort;
    private InetAddress multicastGroup;
    private MulticastSocket multicastSocket;

    public MulticastCommunication(int portNumber) {
        setMulticastPort(portNumber);
        try {
            setMulticastGroup();
        } catch (UnknownHostException exception) {
            System.err.println("Error setting up multicast group");
            exception.printStackTrace();
        }
        try {
            setMulticastSocket();
        } catch (Exception exception) {
            System.err.println("Error setting up multicast socket");
            exception.printStackTrace();
        }
        try {
            joinMulticastGroup();
        } catch (IOException exception) {
            System.err.println("Error joining multicast group");
            exception.printStackTrace();
        }
    }

    // bufferToString is used to retrieve string data form a buffer
    public static StringBuilder bufferToString(byte[] a) {
        if (a == null) return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }

    public int getMulticastPort() {
        return this.multicastPort;
    }

    public void setMulticastPort(int portNumber) {
        this.multicastPort = portNumber;
    }

    public void setMulticastGroup() throws UnknownHostException {
        this.multicastGroup = InetAddress.getByName("228.5.5.5");
    }

    public InetAddress getMulticastGroup() {
        return this.multicastGroup;
    }

    public void sendMulticastMessage(String stringMessage) {
        byte[] byteMessage = stringMessage.getBytes();
        DatagramPacket packetToSend = new DatagramPacket(byteMessage, byteMessage.length, this.multicastGroup, this.multicastPort);
        try {
            multicastSocket.send(packetToSend);
        } catch (IOException exception) {
            System.err.println("Error sending multicast message");
            exception.printStackTrace();
        }
    }

    public String receiveMulticastMessage() {
        byte[] messageToReceive = new byte[65535];
        String returnMessage;
        DatagramPacket emptyPacket = new DatagramPacket(messageToReceive, messageToReceive.length);
        try {
            multicastSocket.receive(emptyPacket);
            returnMessage = bufferToString(messageToReceive).toString();
        } catch (SocketTimeoutException e) {
            returnMessage = "TIMEOUT";
        } catch (IOException exception) {
            System.err.println("Error receiving multicast message");
            exception.printStackTrace();
            returnMessage = "TIMEOUT";
        }
        return returnMessage;
    }

    public InputMessage receiveMulticastMessageExtended() {
        byte[] messageToReceive = new byte[65535];
        String returnMessage;
        DatagramPacket emptyPacket = new DatagramPacket(messageToReceive, messageToReceive.length);
        try {
            multicastSocket.receive(emptyPacket);
            returnMessage = bufferToString(messageToReceive).toString();
        } catch (SocketTimeoutException e) {
            returnMessage = "TIMEOUT";
        } catch (IOException exception) {
            System.err.println("Error receiving multicast message");
            exception.printStackTrace();
            returnMessage = "TIMEOUT";
        }

        return new InputMessage(returnMessage, emptyPacket.getPort(), emptyPacket.getAddress(), emptyPacket.getSocketAddress());
    }

    public void setMulticastSocket() {
        try {
            this.multicastSocket = new MulticastSocket(this.multicastPort);
        } catch (IOException exception) {
            System.err.println("Error setting up multicast socket");
            exception.printStackTrace();
        }
    }

    public void setMulticastSocketTimeOut(int receivedTimeOut) {
        try {
            this.multicastSocket.setSoTimeout(receivedTimeOut);
        } catch (SocketException exception) {
            System.err.println("Error setting up multicast socket timeout");
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void joinMulticastGroup() throws IOException {
        this.multicastSocket.joinGroup(this.multicastGroup);
    }
}
