package com.tylerh;

import com.tylerh.bluetooth.Bluetooth;
import com.tylerh.lwjgl.*;
import com.tylerh.lwjgl.Window;
import com.tylerh.network.Client;
import com.tylerh.network.Server;

import java.awt.*;

/**
 * Created by tsh5949 on 3/8/2016.
 * Last edited on Mar 08, 2016 by tsh5949.
 *
 * Description:
 */
public class Main {

    public static double screenWidth;
    public static double screenHeight;

    private static Client client;

    public static void main(String[] args) {
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = 1280;
        screenHeight = 800;
        //new Thread(new Bluetooth(), "Bluetooth").start();
        //new Thread(new com.tylerh.swing.Window(), "Swing Window").start();
        new Thread("Network") { public void run() { runClient(); }}.start();
        com.tylerh.lwjgl.Window window = new Window();
        window.run();
        //runServer();
    }

    public static void runServer() {
        Server server = new Server(25565);
        server.run();
    }

    public static void runClient() {
        client = new Client("Telemetry Client", "173.74.2.19", 25565);
        client.openConnection();
        while(true) {
            String s = client.receive();

            if (s.startsWith("/dp/")) {
                Window.decodePacket(s);
            }
        }
    }

    public static void sendPacket(byte[] data) {
        client.send(data);
    }
}
