package com.tylerh.swing;

import com.tylerh.Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
        frame.add(new WindowPanel());
        frame.setVisible(true);
    }

    private class WindowPanel extends JPanel {

        private int tempPitch;
        private int tempRoll;
        private int tempYaw;

        private JSlider throttle;
        private JSlider pitch;
        private JSlider roll;
        private JSlider yaw;

        private JLabel throttleLabel;
        private JLabel pitchLabel;
        private JLabel rollLabel;
        private JLabel yawLabel;

        WindowPanel() {
            //setBackground(Color.WHITE);
            setLayout(null);

            throttle = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
            throttle.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider) e.getSource();
                    System.out.println(source.getValue());
                }
            });
            throttle.setMajorTickSpacing(20);
            throttle.setMinorTickSpacing(10);
            throttle.setPaintTicks(true);
            throttle.setPaintLabels(true);
            throttle.setBounds(10, 20, 160, 50);
            add(throttle);

            pitch = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
            pitch.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider) e.getSource();
                    if (tempPitch == source.getValue())
                        pitch.setValue(0);
                    else
                        tempPitch = source.getValue();
                    com.tylerh.lwjgl.Window.setPitch(tempPitch);
                }
            });
            pitch.setMajorTickSpacing(20);
            pitch.setMinorTickSpacing(10);
            pitch.setPaintTicks(true);
            pitch.setPaintLabels(true);
            pitch.setBounds(10, 90, 160, 50);
            add(pitch);

            roll = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
            roll.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider) e.getSource();
                    if (tempRoll == source.getValue())
                        roll.setValue(0);
                    else
                        tempRoll = source.getValue();
                    com.tylerh.lwjgl.Window.setRoll(tempRoll);
                }
            });
            roll.setMajorTickSpacing(20);
            roll.setMinorTickSpacing(10);
            roll.setPaintTicks(true);
            roll.setPaintLabels(true);
            roll.setBounds(10, 160, 160, 50);
            add(roll);

            yaw = new JSlider(JSlider.HORIZONTAL, -50, 50, 0);
            yaw.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider) e.getSource();
                    if (tempYaw == source.getValue())
                        yaw.setValue(0);
                    else
                        tempYaw = source.getValue();
                }
            });
            yaw.setMajorTickSpacing(20);
            yaw.setMinorTickSpacing(10);
            yaw.setPaintTicks(true);
            yaw.setPaintLabels(true);
            yaw.setBounds(10, 230, 160, 50);
            add(yaw);

            throttleLabel = new JLabel("Throttle");
            throttleLabel.setBounds(70, 0, 100, 20);
            add(throttleLabel);

            pitchLabel = new JLabel("Pitch");
            pitchLabel.setBounds(75, 70, 100, 20);
            add(pitchLabel);

            rollLabel = new JLabel("Roll");
            rollLabel.setBounds(77, 140, 100, 20);
            add(rollLabel);

            yawLabel = new JLabel("Yaw");
            yawLabel.setBounds(77, 210, 100, 20);
            add(yawLabel);
        }
    }
}
