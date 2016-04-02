package com.tylerh.bluetooth;

import com.tylerh.swing.Window;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Arrays;

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
    private static int motorData[] = new int[4];

    public static boolean ready = false;
    public static boolean readyToSend = true;

    private int timer = 0;

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

    public static void stop() {
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public static void transmit(String transmitPacket) {
        Bluetooth.transmitPacket = transmitPacket;
    }

    public static double[] getGyroData() {
        return gyroData;
    }
    public static int[] getMotorData() { return motorData; }

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
                            String[] gyData = receivePacket.substring(receivePacket.indexOf("[d"), receivePacket.length()).split(";");
                            gyData[0] = gyData[0].substring(gyData[0].indexOf("[d") + 2, gyData[0].length());
                            gyData[6] = gyData[6].substring(0, gyData[6].length() - 1);

                            gyroData = new double[]{
                                    Double.parseDouble(gyData[0]),
                                    Double.parseDouble(gyData[1]),
                                    Double.parseDouble(gyData[2])};
                            motorData = new int[]{
                                    Integer.parseInt(gyData[3]),
                                    Integer.parseInt(gyData[4]),
                                    Integer.parseInt(gyData[5]),
                                    Integer.parseInt(gyData[6])};
                            System.out.println(Arrays.toString(gyroData));
                            Window.setGyroData(Double.parseDouble(gyData[1]), Double.parseDouble(gyData[2]));
                        } else if (receivePacket.contains("[i")) {
                            //System.out.println(receivePacket.substring(receivePacket.indexOf("[i") + 2, receivePacket.length() - 1));
                            if (receivePacket.contains("DONE"))
                                ready = true;
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
