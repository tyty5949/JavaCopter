package com.tylerh;

import com.tylerh.bluetooth.Bluetooth;
import com.tylerh.lwjgl.*;
import com.tylerh.lwjgl.Window;

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
        //Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = 1280;
        screenHeight = 800;
        new Thread(new Bluetooth(), "Bluetooth").start();
        new Thread(new com.tylerh.swing.Window(), "Swing Window").start();
        //com.tylerh.lwjgl.Window window = new Window();
        //window.run();
    }
}
