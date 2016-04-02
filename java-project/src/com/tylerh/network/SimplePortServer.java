package com.tylerh.network;

/**
 * Created by Tyler on 3/3/14.
 * Project: Network
 */
public class SimplePortServer {

    //TODO - POSSIBLE WRITE A GUI FOR THIS SERVER TYPE,  BUT USE IF WANTED LIKE CREATE GUI METHOD

    /* The port that the server will start on */
    private int port;

    /* Boolean to make sure server can't start if there is an error */
    private boolean canStart;

    /**
     * The constructor to create a new port server
     *
     * @param args - The command line arguments
     */
    public SimplePortServer(String[] args) {
        System.out.println("Initializing basic port server...");

        if (args.length != 1) {
            System.out.println("Initialization failed... (port required)");
            System.out.println("USAGE: java -jar [jar file].jar [port]");
            canStart = false;
            return;
        } else
            canStart = true;

        port = Integer.parseInt(args[0]);
        System.out.println("Initialization finished!");
    }

    /**
     * Method to start the server on the port specified from the command line
     */
    public void start() {
        if (canStart) {
            System.out.println("Starting server...");
            new Server(port);
        }
    }
}