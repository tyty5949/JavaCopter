package com.tylerh.lwjgl;

import com.swinggl.backend.Texture;
import com.swinggl.backend.TrueTypeFont;
import com.swinggl.elements.GLFrame;
import com.swinggl.elements.GLPanel;
import com.swinggl.util.GLColor;
import com.swinggl.util.RenderUtil;
import com.swinggl.util.SpriteSheet;
import com.tylerh.Camera;
import com.tylerh.GLU;
import com.tylerh.Main;
import com.tylerh.bluetooth.Bluetooth;
import com.tylerh.model.OBJModel;
import com.tylerh.model.TexturedOBJModel;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

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

    private static float m1 = 0f;
    private static float m2 = 0f;
    private static float m3 = 0f;
    private static float m4 = 0f;

    private static float throttle = 0f, rollIn = 0f, pitchIn = 0f, yawIn = 0f;

    public Window() {
        frame = new GLFrame(false);
        frame.setSize(1200, 1200);
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

    public static void decodePacket(String packet) {
        String[] gyData = packet.substring(packet.indexOf("[d"), packet.length()).split(";");
        gyData[0] = gyData[0].substring(gyData[0].indexOf("[d") + 2, gyData[0].length());
        gyData[6] = gyData[6].substring(0, gyData[6].indexOf("]"));

        String control = packet.substring(packet.indexOf("[c"), packet.length());

        int[] controlData = new int[] {
                Integer.parseInt(control.substring(2, 5)),
                Integer.parseInt(control.substring(5, 8)) - 50,
                Integer.parseInt(control.substring(8, 11)) - 50,
                Integer.parseInt(control.substring(11, 14)) - 50};

        double[] gyroData = new double[]{
                Double.parseDouble(gyData[0]),
                Double.parseDouble(gyData[1]),
                Double.parseDouble(gyData[2])};
        int[] motorData = new int[]{
                Integer.parseInt(gyData[3]),
                Integer.parseInt(gyData[4]),
                Integer.parseInt(gyData[5]),
                Integer.parseInt(gyData[6])};

        gyroPitch = (float) gyroData[2];
        gyroRoll = (float) gyroData[1];
        m1 = motorData[0];
        m2 = motorData[1];
        m3 = motorData[2];
        m4 = motorData[3];
        throttle = controlData[0];
        rollIn = controlData[1];
        pitchIn = controlData[2];
        yawIn = controlData[3];
    }

    private class WindowPanel extends GLPanel {

        private Texture spritesheet;
        private float[] backgroundCoords;
        private float[] horizonCoords;

        private float[] redCircle;
        private float[] greenCircle;

        private Camera camera;

        private Texture baseTexture;
        private Texture copterTexture;
        private TexturedOBJModel groundModel;
        private OBJModel copterModel;

        boolean firstUpdate;

        private Lighting lighting;

        private TrueTypeFont font;

        private DecimalFormat df = new DecimalFormat("#.#");

        @Override
        public void init(GLFrame frame) {
            spritesheet = new Texture("res/spritesheet.png");
            baseTexture = new Texture("res/base_texture.png");
            copterTexture = new Texture("res/quad_texture.png");

            backgroundCoords = SpriteSheet.getRectCoords(0, 0, 600, 600, spritesheet);
            horizonCoords = SpriteSheet.getRectCoords(600, 0, 300, 600, spritesheet);
            redCircle = SpriteSheet.getRectCoords(90, 600, 90, 90, spritesheet);
            greenCircle = SpriteSheet.getRectCoords(0, 600, 90, 90, spritesheet);

            groundModel = new TexturedOBJModel("res/ground_model.obj");
            copterModel = new OBJModel("res/copter_model.obj");

            camera = new Camera(0f, -2.2f, -6.5f);
            camera.setPitch(0f);

            lighting = new Lighting();
            lighting.setLightLocation(100, 100, 100);

            font = new TrueTypeFont("res/font.ttf", 18);

            this.initialized = true;
        }

        @Override
        public void update(GLFrame frame, float delta) {
            if(!firstUpdate) {
                //new Thread(new com.tylerh.swing.Window(), "Swing Window").start();
                firstUpdate = true;
            }
        }

        @Override
        public void render(GLFrame frame, float delta) {
            switchTo3D();
            GL11.glPushMatrix();
            camera.lookThrough();
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            baseTexture.bind();

            groundModel.render();

            GL11.glDisable(GL11.GL_TEXTURE_2D);

            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            lighting.tick();
            lighting.enable();
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 1.5f, 0f);
            GL11.glRotatef(gyroPitch, 1f, 0f, 0f);
            GL11.glRotatef(-gyroRoll, 0f, 0f, 1f);
            GL11.glRotatef(45, 0f, 1f, 0f);
            GL11.glScalef(.3f, .3f, .3f);
            copterModel.render();
            GL11.glPopMatrix();

            GL11.glPopMatrix();

            lighting.disable();
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);

            switchTo2D();

            GL11.glPushMatrix();
            GL11.glScalef(2f,2f,2f);

            GL11.glColor4f(.2039f, .3647f, .2039f, 1f);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(0f, 0f);
            GL11.glVertex2f(240f, 0f);
            GL11.glVertex2f(240f, 240f);
            GL11.glVertex2f(0f, 240f);
            GL11.glEnd();

            GL11.glPushMatrix();
            GL11.glTranslated(138f, 138f + gyroPitch, 0f);
            GL11.glRotated(gyroRoll, 0f, 0f, 1f);
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

            GL11.glPushMatrix();
            GL11.glTranslatef(311f, 61f, 0f);
            GL11.glScalef(m1 / 50, m1 / 50, m1 / 50);
            GL11.glBegin(GL11.GL_QUADS);
            RenderUtil.drawImmediateTexture(-45f, -45f, 90f, 90f, greenCircle);
            GL11.glEnd();
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(410f, 61f, 0f);
            GL11.glScalef(m2 / 50, m2 / 50, m2 / 50);
            GL11.glBegin(GL11.GL_QUADS);
            RenderUtil.drawImmediateTexture(-45f, -45f, 90f, 90f, redCircle);
            GL11.glEnd();
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(410f, 227f, 0f);
            GL11.glScalef(m3 / 50, m3 / 50, m3 / 50);
            GL11.glBegin(GL11.GL_QUADS);
            RenderUtil.drawImmediateTexture(-45f, -45f, 90f, 90f, greenCircle);
            GL11.glEnd();
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(311f, 227f, 0f);
            GL11.glScalef(m4 / 50, m4 / 50, m4 / 50);
            GL11.glBegin(GL11.GL_QUADS);
            RenderUtil.drawImmediateTexture(-45f, -45f, 90f, 90f, redCircle);
            GL11.glEnd();
            GL11.glPopMatrix();

            font.drawString("JavaCopter v1.3", 4, 2, GLColor.LIGHT_GRAY);

            font.drawString("Roll: " + gyroRoll + "°", 40, 240f, GLColor.LIGHT_GRAY);
            font.drawString("Pitch: " + gyroPitch + "°", 40, 256f, GLColor.LIGHT_GRAY);
            font.drawString("Motor 1:      " + df.format(m1 / 255 * 100) + "%", 466, 62, GLColor.LIGHT_GRAY);
            font.drawString("Motor 2:      " + df.format(m2 / 255 * 100) + "%", 466, 78, GLColor.LIGHT_GRAY);
            font.drawString("Motor 3:      " + df.format(m3 / 255 * 100) + "%", 466, 94, GLColor.LIGHT_GRAY);
            font.drawString("Motor 4:      " + df.format(m4 / 255 * 100) + "%", 466, 110, GLColor.LIGHT_GRAY);

            font.drawString("Throttle:     " + df.format(throttle) + "%", 466, 150, GLColor.LIGHT_GRAY);
            font.drawString("Roll in:        " + df.format(rollIn / 50 * 100) + "%", 466, 166, GLColor.LIGHT_GRAY);
            font.drawString("Pitch in:     " + df.format(pitchIn / 50 * 100) + "%", 466, 182, GLColor.LIGHT_GRAY);
            font.drawString("Yaw in:       " + df.format(yawIn / 50 * 100) + "%", 466, 198, GLColor.LIGHT_GRAY);

            RenderUtil.disableTransparency();
            GL11.glPopMatrix();
        }

        @Override
        public void dispose() {

        }
    }
}
