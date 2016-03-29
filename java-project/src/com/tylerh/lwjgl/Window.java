package com.tylerh.lwjgl;

import com.swinggl.backend.Texture;
import com.swinggl.elements.GLFrame;
import com.swinggl.elements.GLPanel;
import com.swinggl.util.GLColor;
import com.swinggl.util.RenderUtil;
import com.swinggl.util.SpriteSheet;
import com.tylerh.Camera;
import com.tylerh.GLU;
import com.tylerh.Main;
import com.tylerh.model.TexturedOBJModel;
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

    private static float gyroRoll = 0f;
    private static float gyroPitch = 0f;
    private static float gyroYaw = 0f;

    public Window() {
        frame = new GLFrame(false);
        frame.setSize(600, 600);
        frame.setPosition((int) (Main.screenWidth / 2) - 410, (int) (Main.screenHeight / 2) - 300);
        frame.setTitle("Info");
        frame.setBackgroundColor(GLColor.BLACK);
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
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0f, frame.getSize()[0], frame.getSize()[1], 0.0f, 0.0f, 1.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    public static void setRoll(float roll) {
        gyroRoll = roll;
    }

    public static void setPitch(float pitch) {
        gyroPitch = pitch;
    }

    private class WindowPanel extends GLPanel {

        private Texture spritesheet;
        private float[] backgroundCoords;
        private float[] horizonCoords;

        private Camera camera;

        private Texture baseTexture;
        private TexturedOBJModel groundModel;
        private TexturedOBJModel copterModel;

        @Override
        public void init(GLFrame frame) {
            spritesheet = new Texture("res/spritesheet.png");
            baseTexture = new Texture("res/base_texture.png");

            backgroundCoords = SpriteSheet.getRectCoords(0, 0, 600, 600, spritesheet);
            horizonCoords = SpriteSheet.getRectCoords(600, 0, 300, 600, spritesheet);

            groundModel = new TexturedOBJModel("res/ground_model.obj");
            //copterModel = new TexturedOBJModel("res/copter_model.obj");

            camera = new Camera(0f, -2f, -6f);
            camera.setPitch(5f);

            this.initialized = true;
        }

        @Override
        public void update(GLFrame frame, float delta) {

        }

        @Override
        public void render(GLFrame frame, float delta) {
            switchTo3D();
            GL11.glPushMatrix();
            camera.lookThrough();
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            baseTexture.bind();

            groundModel.render();
            //copterModel.render();

            GL11.glPopMatrix();
            switchTo2D();

            GL11.glDisable(GL11.GL_TEXTURE_2D);

            GL11.glColor4f(.2039f, .3647f, .2039f, 1f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0f, 0f);
            GL11.glVertex2f(240f, 0f);
            GL11.glVertex2f(240f, 240f);
            GL11.glVertex2f(0f, 240f);
            GL11.glEnd();

            GL11.glPushMatrix();
            GL11.glTranslatef(138f, 138f + gyroPitch, 0f);
            GL11.glRotatef(gyroRoll, 0f, 0f, 1f);
            GL11.glTranslatef(-150f, -240f, 0f);
            GL11.glColor4f(.4078f, .5333f, .7882f, 1f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0f, 0f);
            GL11.glVertex2f(300f, 0f);
            GL11.glVertex2f(300f, 240f);
            GL11.glVertex2f(0f, 240f);
            GL11.glEnd();
            GL11.glPopMatrix();

            GL11.glEnable(GL11.GL_TEXTURE_2D);

            RenderUtil.enableTransparency();
            spritesheet.bind();
            GL11.glColor3f(1f, 1f, 1f);
            GL11.glBegin(GL11.GL_QUADS);
            RenderUtil.drawImmediateTexture(0f, 0f, 600f, 600f, backgroundCoords);
            GL11.glEnd();
            RenderUtil.disableTransparency();
        }

        @Override
        public void dispose() {

        }
    }
}
