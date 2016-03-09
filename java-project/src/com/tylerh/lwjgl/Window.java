package com.tylerh.lwjgl;

import com.swinggl.elements.GLFrame;
import com.swinggl.elements.GLPanel;
import com.tylerh.Main;
import org.lwjgl.glfw.GLFWWindowCloseCallback;

/**
 * Created by tsh5949 on 3/8/2016.
 * Last edited on Mar 08, 2016 by tsh5949.
 *
 * Description:
 */
public class Window implements Runnable{

    private GLFrame frame;

    public Window() {
        frame = new GLFrame(false);
        frame.setSize(600, 600);
        frame.setPosition((int) (Main.screenWidth / 2) - 410, (int) (Main.screenHeight / 2) - 300);
        frame.setTitle("Info");
        frame.setPanel(new WindowPanel());
        frame.setWindowCloseCallback(new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long l) {
                System.exit(0);
            }
        });
    }

    @Override
    public void run() {
        frame.run();
    }

    public class WindowPanel extends GLPanel {

        @Override
        public void init(GLFrame frame) {

        }

        @Override
        public void update(GLFrame frame, float delta) {

        }

        @Override
        public void render(GLFrame frame, float delta) {

        }

        @Override
        public void dispose() {

        }
    }
}
