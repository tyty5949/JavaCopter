package com.tylerh.network;

import com.tylerh.util.UniqueIdentifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Tyler on 3/3/14.
 * Project: Network
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class Server implements Runnable {

    /* The maximum amount of failed attempts the server will make to ping the client until disconnecting it */
    private final int MAX_ATTEMPTS = 5;

    /* The list of clients that the server is connected to */
    private List<ServerClient> clients = new ArrayList<ServerClient>();

    /* The most recent list of clients that responded to the ping */
    private List<Integer> clientResponse = new ArrayList<Integer>();

    /* The socket that the server will be running on */
    private DatagramSocket socket;

    /* The port that the server will be running on */
    private int port;

    /* The running status of the server */
    private boolean running = false;

    /* The multiple threads for packet and client managing services */
    private Thread run, manage, send, receive, console;

    //private int id;
    //private boolean status;

    /**
     * The constructor that will initialize the server
     *
     * @param port - The port to start the server on
     */
    public Server(int port) {
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        run = new Thread(this, "Server");
        run.start();
    }

    /**
     * The method that essentially starts the server client and packet receiving threads
     */
    public void run() {
        running = true;
        System.out.println("Server started on port: " + port);
        manageClients();
        receive();
        startConsole();
    }

    /**
     * Method that will manager the clients to see if they are still connected, sends a ping packet
     */
    private void manageClients() {
        manage = new Thread("Manage") {
            public void run() {
                //noinspection StatementWithEmptyBody
                while (running) {
                    sendToAll("/p/server ping/e/".getBytes());
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0; i < clients.size(); i++) {
                        ServerClient tempClient = clients.get(i);
                        if (!clientResponse.contains(tempClient.getID())) {
                            if (tempClient.getAttempt() >= MAX_ATTEMPTS)
                                disconnect(tempClient.getID(), false);
                            else
                                tempClient.setAttempt(tempClient.getAttempt() + 1);
                        } else {
                            clientResponse.remove(new Integer(tempClient.getID()));
                            tempClient.setAttempt(0);
                        }
                    }
                }
            }
        };
        manage.start();
    }

    /**
     * Method that will receive the packets as they come into the server
     */
    private void receive() {
        receive = new Thread("Receive") {
            public void run() {
                while (running) {
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    processPacket(packet);
                }
            }
        };
        receive.start();
    }

    /**
     * Method that runs the console on a different thread than all of the packet managers
     */
    private void startConsole() {
        console = new Thread("Console") {
            @SuppressWarnings("ForLoopReplaceableByForEach")
            public void run() {
                String input;
                Scanner scanner = new Scanner(System.in);
                while (running) {
                    input = scanner.nextLine();
                    if (input.equals("help")) {
                        System.out.println("Welcome to the Server command help!");
                        System.out.println("Usage: [command] <parameter>");
                        System.out.println("\n---COMMANDS--");
                        System.out.println("help - Shows the help menu for all the commands");
                        System.out.println("exit - Exits the server and kicks all connected clients");
                        System.out.println("kick <client_id> - Kicks the client with specified id");
                        System.out.println("port - Shows the current port the server is running on");
                        System.out.println("address - Shows the current address(s) of the server");
                        System.out.println("clients - Shows the current user(s) online/displaying their ID");
                    } else if (input.equals("exit")) {

                    } else if (input.startsWith("kick")) {
                        int clientID = Integer.parseInt(input.substring(5, input.length()));
                        ServerClient c = null;
                        for (int i = 0; i < clients.size(); i++) {
                            if (clients.get(i).getID() == clientID)
                                c = clients.get(i);
                        }
                        if (c == null) {
                            System.out.println("No client with the ID " + clientID + " was available to kick!");
                            continue;
                        }
                        send("/k//e/".getBytes(), c.getAddress(), c.getPort());
                    } else if (input.equals("port")) {
                        System.out.println("The server is running on port " + port + ".");
                    } else if (input.equals("address")) {
                        BufferedReader in = null;
                        try {
                            URL whatismyip = new URL("http://checkip.amazonaws.com");
                            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                            String ip = in.readLine();
                            System.out.println("Server is running on the external IP of: " + ip + ":" + port);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (input.equals("clients")) {
                        System.out.println("Found " + clients.size() + " clients.");
                        for (int i = 0; i < clients.size(); i++) {
                            ServerClient c = clients.get(i);
                            System.out.println(c.getName() + "(" + c.getID() + "): " + c.getAddress() + ":" + c.getPort());
                        }
                    } else {
                        System.out.println("That is not a command, type 'help' for more...");
                    }
                }
            }
        };
        console.start();
    }

    /**
     * Method to send the packet to a certain client specified by the address and port
     *
     * @param data    - The data of packet to be sent
     * @param address - The address of client
     * @param port    - The port of the client
     */
    private void send(final byte[] data, final InetAddress address, final int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    /**
     * Method to send a packet to every client in the client list
     *
     * @param data - The data for the packet as a byte array
     */
    private void sendToAll(final byte[] data) {
        for (ServerClient client : clients) {
            send(data, client.getAddress(), client.getPort());
        }
    }

    /**
     * Method that will disconnect a client from the server
     *
     * @param ID     - The ID of the client
     * @param status - 'true' if client disconnected, 'false' if client timed out
     */
    private void disconnect(int ID, boolean status) {
        ServerClient client = null;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getID() == ID) {
                client = clients.get(i);
                clients.remove(i);
                break;
            }
        }
        assert client != null;
        if (status) {
            System.out.println("Client: " + client.getName() + " (" + client.getID() + ") @ " + client.getAddress() + ":" + client.getPort() + " disconnected.");
        } else {
            System.out.println("Client: " + client.getName() + " (" + client.getID() + ") @ " + client.getAddress() + ":" + client.getPort() + " timed out.");
        }
    }

    //TODO - CREATE MORE PACKETS

    /**
     * Method that is used for processing packets that will be sent to the server
     * TO USE DIFFERENT PACKETS YOU MUST ADD ELSE IF STATEMENTS TO THE MAIN IF STATEMENTS TO IDENTIFY THEM
     * THEN IN THE BODY THE PACKETS WILL DO DIFFERENT THINGS LIKE SEND PACKETS TO THE CLIENTS
     * WARNING - NOT RECOMMENDED
     * ----------------------------------------------------------------------------------------------------------------
     * Current packets:
     * IMPORTANT - /e/ MUST BE ADDED TO THE END OF EVERY PACKET
     * /c/ - The default connection packet, adds the client to server, server responds to client with ID
     * /d/ - The default disconnect packet, removes the client from the client list
     * /p/ - Is reserved for server to client pinging ONLY, DO NOT USE
     * /k/ - Is reserved for server to client kicking ONLY, DO NOT USE
     * /sa/ - A send all packet that will send the packet to every client (replaces /sa/ with /m/)
     * /s/ - A packet that will send to the desired client (uses client ID) ex. /s/&1020&.../e/ (replaces /s/ with /m/)
     * /m/ - Used to message the client with some sort of data from other clients (when client sends console /sa/ of /s/
     * the server will respond to the requested client(s) WITH /m/ NOT /sa/ or /s/)
     *
     * @param packet - The packet that was sent to the server
     */
    private void processPacket(DatagramPacket packet) {
        String data = new String(packet.getData());
        if (data.startsWith("/c/")) {
            int ID = UniqueIdentifier.getIdentifier();
            clients.add(new ServerClient(data.split("/c/|/e/")[1], packet.getAddress(), packet.getPort(), ID));
            System.out.println("Client connected with the name: '" + data.split("/c/|/e/")[1] + "' @ " + packet.getAddress() + ":" + packet.getPort() + " and is now using the ID: " + ID);
            send(("/c/" + ID + "/e/").getBytes(), packet.getAddress(), packet.getPort());
        } else if (data.startsWith("/d/")) {
            disconnect(Integer.parseInt(data.split("/d/|/e/")[1]), true);
        } else if (data.startsWith("/sa/")) {
            sendToAll(("/m/" + data.split("/sa/|/e/")[1] + "/e/").getBytes());
        } else if (data.startsWith("/s/")) {        //TODO - FIX INDIVIDUAL PACKET SENDING
            String decodedData = data.split("/s/|/e/")[1];
            String targetID = decodedData;
            targetID = targetID.substring(targetID.indexOf("&") + 1);
            targetID = targetID.substring(0, targetID.indexOf("&"));
            int id = Integer.parseInt(targetID);
            decodedData = decodedData.replace("&" + id + "&", "");
            ServerClient targetClient = null;
            for (ServerClient item : clients) {
                if (item.getID() == id) {
                    targetClient = item;
                    break;
                }
            }
            assert targetClient != null;
            send(decodedData.getBytes(), targetClient.getAddress(), targetClient.getPort());
        } else if (data.startsWith("/p/")) {
            clientResponse.add(Integer.parseInt(data.split("/p/|/e/")[1]));
        } else if (data.startsWith("/dp/")) {
            sendToAll(data.substring(4, data.length() - 3).getBytes());
        } else {
            System.out.println(packet.getAddress() + ":" + packet.getPort() + " sent an unknown packet: " + data);
        }
    }
}