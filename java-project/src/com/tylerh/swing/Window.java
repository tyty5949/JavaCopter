package com.tylerh.swing;

import com.tylerh.Main;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tsh5949 on 3/8/2016.
 * Last edited on Mar 08, 2016 by tsh5949.
 *
 * Description:
 */
public class Window implements Runnable {

    private JFrame frame;

    public Window() {

    }

    @Override
    public void run() {
        frame = new JFrame("Control");
        frame.setSize(200, 634);
        frame.setLocation((int) (Main.screenWidth / 2) + 200, (int) (Main.screenHeight / 2) - 328);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBackground(Color.WHITE);
        frame.setVisible(true);
    }
}
