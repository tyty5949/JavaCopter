package com.tylerh;

import com.tylerh.bluetooth.Bluetooth;

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

    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.getWidth();
        screenHeight = screenSize.getHeight();
        new Thread(new com.tylerh.lwjgl.Window(), "LWJGL Window").start();
        new Thread(new com.tylerh.swing.Window(), "Swing Window").start();
        new Bluetooth().run();
    }
}
