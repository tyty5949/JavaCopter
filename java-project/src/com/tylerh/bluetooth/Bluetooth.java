package com.tylerh.bluetooth;

import com.tylerh.Main;
import com.tylerh.swing.Window;
import jssc.SerialPort;
import jssc.SerialPortException;

public class Bluetooth implements Runnable {

    // The SerialPort object that does the communication with the bluetooth port
    private static SerialPort serialPort;

    // The packet that is being transmitted to the arduino
    private static String transmitPacket = "";

    // Becomes true after the arduino finished initializing
    private static boolean ready = false;

    public Bluetooth() {
        serialPort = new SerialPort("/dev/tty.HC-06-DevB");     // The port of the arduino bluetooth serial on the mac
    }

    /**
     * The writer is on a different thread so that serial transmitting and receiving can be done at the same time.
     */
    public void run() {
        try {
            // Open the Serial Port
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0);

            // Start the SerialPortReader
            new Thread(new SerialPortReader(), "SerialPortReader").start();

            // Sends the initial connection packets
            serialPort.writeBytes("[conn]".getBytes());
            serialPort.writeBytes("[conn]".getBytes());
            serialPort.writeBytes("[conn]".getBytes());

            // Transmit control packets
            while (true) {
                if (!transmitPacket.equals("")) {
                    serialPort.writeBytes(transmitPacket.getBytes());
                }
                Thread.sleep(100);
            }
        } catch (SerialPortException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }


    public static void transmit(String transmitPacket) {
        Bluetooth.transmitPacket = transmitPacket;
    }

    public static boolean isReady() {
        return ready;
    }

    /**
     * The reader is on a different thread so that serial transmitting and receiving can be done at the same time.
     */
    static class SerialPortReader implements Runnable {

        private String receivePacket = "";

        @Override
        public void run() {
            try {
                byte buffer[];
                while (true) {
                    // When a byte arrives in the serial, it is read into a buffer
                    buffer = serialPort.readBytes(1);
                    receivePacket += (char) buffer[0];

                    // If a complete packet has arrived
                    if (receivePacket.contains("[") && receivePacket.contains("]")) {
                        // If the received packet is a data packet
                        if (receivePacket.contains("[d")) {
                            String[] gyData = receivePacket.substring(receivePacket.indexOf("[d"), receivePacket.length()).split(";");
                            Window.setGyroData(Double.parseDouble(gyData[1]), Double.parseDouble(gyData[2]));
                            Main.sendPacket(("/dp/" + receivePacket + transmitPacket + "/e/").getBytes());
                        }

                        // If the received packet is a information packet
                        if (receivePacket.contains("[i")) {
                            System.out.println(receivePacket.substring(receivePacket.indexOf("[i") + 2, receivePacket.length() - 1));

                            // If the arduino has finished initializing, the quadcopter is now open to control packets
                            if (receivePacket.contains("DONE"))
                                ready = true;
                        }

                        // Clear the packet once it has been read
                        receivePacket = "";
                    }
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
