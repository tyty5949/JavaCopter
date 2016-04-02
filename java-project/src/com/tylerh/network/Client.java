package com.blockydigital.engine.network;

import java.io.IOException;
import java.net.*;

/**
 * Created by Tyler on 3/3/14.
 * Project: Network
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal", "SynchronizeOnNonFinalField"})
public class Client {

    /* The name of the client */
    private String name;

    /* The ip address of the server (AS A STRING) for the client to connect to */
    private String address;

    /* The port for the client to connect to */
    private int port;

    /* The unique ID of the client */
    private int ID;

    /* The socket that the client will run on */
    private DatagramSocket socket;

    /* The ip of the server (AS A IPnetAddress) for the client to connect to */
    private InetAddress ip;

    /* The various threads that the different network utilities will run on */
    private Thread send, close;

    /**
     * The Constructor to create the client
     *
     * @param name    - The name of the client
     * @param address - The address of the client
     * @param port    - The port of the client
     */
    public Client(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    /**
     * Method to get the name of the client
     *
     * @return - The name of the client
     */
    public String getName() {
        return name;
    }

    /**
     * Method to get the address of the server the client will connect to
     *
     * @return - The address of the requested server (AS A STRING)
     */
    public String getAddress() {
        return address;
    }

    /**
     * Method to get the port of the server the client will connect to
     *
     * @return - The port of the requested server
     */
    public int getPort() {
        return port;
    }

    /**
     * Method to get the ID of the client
     *
     * @return - The ID of the client
     */
    public int getID() {
        return ID;
    }

    /**
     * Method to set the ID of the client
     *
     * @param ID - The new ID of the client
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    //TODO - DISCONNECT/RETRY CERTAIN NUMBER OF TIMES IF SERVER IS NOT AVAILABLE

    /**
     * Method to open the connect of the client to the server
     *
     * @return - If the connection succeeded or not
     */
    public boolean openConnection() {
        try {
            socket = new DatagramSocket();
            ip = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Attempting a connection to " + address + ":" + port);
        send(("/c/" + name + "/e/").getBytes());
        return true;
    }

    /**
     * Method to send data to the server as a packet
     *
     * @param data - The data in form of a byte array
     */
    public void send(final byte[] data) {
        send = new Thread("Send") {
            DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);

            public void run() {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    //TODO - CREATE AUTOMATIC PACKET READING FOR THINGS LIKE SETTING THE ID

    /**
     * Method to receive data from the server as a packet
     * THE MAXIMUM SIZE OF A SINGLE PACKET THAT CAN BE SENT IS 1024 BYTES
     *
     * @return - The data in the form of a string, WILL CUT OUT PACKET NOTATION
     */
    public String receive() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, 1024);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(packet.getData());
        if (received.startsWith("/c/")) {
            ID = Integer.parseInt(received.split("/c/|/e/")[1]);
            System.out.println("Successfully established connection with server. Client assigned ID: " + ID);
        } else if (received.startsWith("/p/"))
            send(("/p/" + ID + "/e/").getBytes());
        else if (received.startsWith("/k/"))
            close();
        else if (received.startsWith("/m/"))
            received = received.split("/m/|/e/")[1];
        return received;
    }

    /**
     * Method to close the socket and clean up the client
     */
    public void close() {
        close = new Thread("Close") {
            public void run() {
                send(("/d/" + ID + "/e/").getBytes());
                synchronized (socket) {
                    socket.close();
                }
            }
        };
        close.start();
    }

    /**
     * Method that is used to check if the client is still connected
     *
     * @return - Whether or not the socket is still connected
     */
    public boolean isConnected() {
        return !socket.isClosed();
    }
}