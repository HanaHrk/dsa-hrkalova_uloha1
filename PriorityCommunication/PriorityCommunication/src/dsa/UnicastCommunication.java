package dsa;

import java.io.IOException;
import java.net.*;
import java.util.List;

public final class UnicastCommunication {


    private int unicastPort;
    private InetAddress localIP;
    private DatagramSocket unicastSocket;

    public UnicastCommunication(int portNumber, int timeOut) {
        setUnicastPort(portNumber);
        setLocalIP("localhost");
        setUnicastSocket();
        setUnicastSocketTimeOut(timeOut);
    }

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

    public int getUnicastPort() {
        return this.unicastPort;
    }

    public void setUnicastPort(int portNumber) {
        this.unicastPort = portNumber;
    }

    public InetAddress getLocalIP() {
        return this.localIP;
    }

    public void setLocalIP(String hostName) {
        try {
            this.localIP = InetAddress.getByName(hostName);
        } catch (UnknownHostException exception) {
            System.err.println("Error setting the IP");
            exception.printStackTrace();
        }
    }

    public void setUnicastSocket() {
        try {
            this.unicastSocket = new DatagramSocket(unicastPort, localIP);
        } catch (SocketException exception) {
            System.err.println("Error setting up unicast socket");
            exception.printStackTrace();
        }
    }

    public DatagramSocket getUnicastSocket() {
        return this.unicastSocket;
    }

    public void setUnicastSocketTimeOut(int newTimeOut) {
        try {
            this.unicastSocket.setSoTimeout(newTimeOut);
        } catch (SocketException exception) {
            System.err.println("Error setting up unicast socket timeout");
            exception.printStackTrace();
        }
    }

    public void sendUnicastMessage(String stringMessage, InetAddress addressToSend, int portToSend) {
        byte[] byteMessage = stringMessage.getBytes();
        DatagramPacket packetToSend = new DatagramPacket(byteMessage, byteMessage.length, addressToSend, portToSend);
        try {
            this.unicastSocket.send(packetToSend);
        } catch (IOException exception) {
            System.err.println("Error sending unicast message");
            exception.printStackTrace();
        }
    }

    public void broadcastMessage(String stringMessage, InetAddress addressToSend, List<Integer> portList) {
        byte[] byteMessage = stringMessage.getBytes();
        for (Integer portValue : portList) {
            if (((Math.random() * 10) > 0.0)) { // Random simulation of communication failure
                DatagramPacket packetToSend = new DatagramPacket(byteMessage, byteMessage.length, addressToSend, portValue);
                try {
                    this.unicastSocket.send(packetToSend);
                } catch (IOException exception) {
                    System.err.println("Error sending unicast broadcast message");
                    exception.printStackTrace();
                }
            }
        }
    }

    public String receiveUnicastMessage() {
        byte[] messageToReceive = new byte[65535];
        String returnMessage;
        DatagramPacket emptyPacket = new DatagramPacket(messageToReceive, messageToReceive.length);
        try {
            this.unicastSocket.receive(emptyPacket);
            returnMessage = bufferToString(messageToReceive).toString();
        } catch (SocketTimeoutException e) {
            returnMessage = "TIMEOUT";
        } catch (IOException exception) {
            returnMessage = "TIMEOUT";
            System.err.println("Error receiving unicast message");
            exception.printStackTrace();
        }
        return returnMessage;
    }

}
