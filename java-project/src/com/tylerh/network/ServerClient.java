package com.blockydigital.engine.network;

import java.net.InetAddress;

/**
 * Created by Tyler on 3/3/14.
 * Project: Network
 */
@SuppressWarnings("UnusedDeclaration")
public class ServerClient {

    /* The name of the client */
    private String name;

    /* The ip address of the client */
    private InetAddress address;

    /* The port of the client */
    private int port;

    /* The ID of the client (MUST BE UNIQUE) */
    private final int ID;

    /* The number of attempts to ping the client */
    private int attempt;

    /**
     * The constructor to create a new client for the server to handle
     *
     * @param name    - The name of the client
     * @param address - The IP address of the client
     * @param port    - The port of the client
     * @param ID      - The unique ID of the client
     */
    public ServerClient(String name, InetAddress address, int port, final int ID) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.ID = ID;
    }

    /**
     * Method to get the name of the client
     *
     * @return - The clients name
     */
    public String getName() {
        return name;
    }

    /**
     * Method to get the IP address of the client
     *
     * @return - The address of the client
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Method to get the port of the client
     *
     * @return - The port of the client
     */
    public int getPort() {
        return port;
    }

    /**
     * Method to get the unique ID of the client
     *
     * @return - The ID of the client
     */
    public int getID() {
        return ID;
    }

    /**
     * Method to get the total number of non responsive pings
     *
     * @return - The number of pings
     */
    public int getAttempt() {
        return attempt;
    }

    /**
     * Method to increment the total number of non responsive pings
     *
     * @param increment - The number to increment by
     */
    public void setAttempt(int increment) {
        attempt += increment;
    }
}