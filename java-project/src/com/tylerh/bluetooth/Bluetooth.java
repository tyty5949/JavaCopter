package com.tylerh.bluetooth;

import com.tylerh.swing.Window;
import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Created by tsh5949 on 3/8/2016.
 * Last edited on Mar 08, 2016 by tsh5949.
 * <p/>
 * Description:
 */
public class Bluetooth implements Runnable {

    private static SerialPort serialPort;

    private static String transmitPacket = "";

    private static double gyroData[] = new double[3];

    public static boolean ready = false;

    public Bluetooth() {
        serialPort = new SerialPort("/dev/tty.HC-06-DevB");
    }

    @Override
    public void run() {
        try {
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0);

            // SerialPortReader
            new Thread(new SerialPortReader(), "SerialPortReader").start();

            System.out.println("Opened reader, sending connection packet");
            serialPort.writeBytes("[conn]".getBytes());
            System.out.println("sent connection packet");
            ready = true;

            // Transmit control packets
            while (true) {
                if (!transmitPacket.equals("")) {
                    //serialPort.writeBytes(transmitPacket.getBytes());
                    transmitPacket = "";
                }

                Thread.sleep(5);
            }
        } catch (SerialPortException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void transmit(String transmitPacket) {
        Bluetooth.transmitPacket = transmitPacket;
    }

    public static double[] getGyroData() {
        return gyroData;
    }

    static class SerialPortReader implements Runnable {

        private String receivePacket = "";

        @Override
        public void run() {
            try {
                byte buffer[];
                while (true) {
                    buffer = serialPort.readBytes(1);
                    System.out.print((char) buffer[0]);
                    receivePacket += (char) buffer[0];
                    //System.out.println(receivePacket);
                    if (receivePacket.contains("[") && receivePacket.contains("]")) {
                        //System.out.println(receivePacket);
                        if (receivePacket.contains("[d")) {
                            String[] gyData = receivePacket.split(";");
                            gyData[0] = gyData[0].substring(gyData[0].indexOf("[d") + 2, gyData[0].length());
                            gyData[2] = gyData[2].substring(0, gyData[2].length() - 1);
                            //System.out.println("X: " + gyData[0]);
                            //System.out.println("Y: " + gyData[1]);
                            //System.out.println("Z: " + gyData[2] + "\n");
                            if (!gyData[0].contains("INF") && !gyData[1].contains("INF") && !gyData[2].contains("INF")) {
                                gyroData = new double[]{
                                        Double.parseDouble(gyData[0]),
                                        Double.parseDouble(gyData[1]),
                                        Double.parseDouble(gyData[2])};
                                Window.setGyroData(Double.parseDouble(gyData[1]), Double.parseDouble(gyData[2]));
                            }
                        } else if (receivePacket.contains("[i")) {
                            System.out.println(receivePacket.substring(receivePacket.indexOf("[i") + 2, receivePacket.length() - 1));
                        }
                        receivePacket = "";
                    }
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        }
    }
}
