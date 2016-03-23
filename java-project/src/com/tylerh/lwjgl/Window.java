package com.tylerh.lwjgl;

import com.swinggl.backend.Texture;
import com.swinggl.elements.GLFrame;
import com.swinggl.elements.GLPanel;
import com.swinggl.util.RenderUtil;
import com.swinggl.util.SpriteSheet;
import com.tylerh.GLU;
import com.tylerh.Main;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL11;

/**
 * Created by tsh5949 on 3/8/2016.
 * Last edited on Mar 08, 2016 by tsh5949.
 *
 * Description:
 */
public class Window implements Runnable {

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

    private void switchTo3D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1.0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity(); // Reset The Projection Matrix
        GLU.gluPerspective(45.0f, (frame.getSize()[0] / frame.getSize()[1]), 0.1f, 100.0f); // Calculate The Aspect Ratio Of The Window
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
    }

    private void switchTo2D() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0f, frame.getSize()[0], frame.getSize()[1], 0.0f, 0.0f, 1.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private class WindowPanel extends GLPanel {

        private float gyroRoll;
        private float gyroPitch;
        private float gyroYaw;

        private Texture spritesheet;
        private float[] backgroundCoords;
        private float[] horizonCoords;

        @Override
        public void init(GLFrame frame) {
            spritesheet = new Texture("res/spritesheet.png");
            backgroundCoords = SpriteSheet.getRectCoords(0, 0, 600, 600, spritesheet);

            horizonCoords = SpriteSheet.getRectCoords(600, 0, 300, 600, spritesheet);

            this.initialized = true;
        }

        @Override
        public void update(GLFrame frame, float delta) {

        }

        @Override
        public void render(GLFrame frame, float delta) {
            RenderUtil.enableTransparency();
            spritesheet.bind();
            GL11.glColor3f(1f, 1f, 1f);
            GL11.glBegin(GL11.GL_QUADS);
            RenderUtil.drawImmediateTexture(0f, 0f, 600f, 600f, backgroundCoords);
            GL11.glEnd();
            RenderUtil.disableTransparency();

            switchTo3D();
            //3D models


            switchTo2D();
        }

        @Override
        public void dispose() {

        }
    }
}
