package com.tylerh.lwjgl;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.GL_AMBIENT_AND_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_TWO_SIDE;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLightModeli;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Lighting {

    public ArrayList<LightSource> lights;

    public Lighting() {
        lights = new ArrayList<LightSource>();
        lights.add(new LightSource(0f, 0f, 0f, 1f));

        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_LIGHTING);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        glEnable(GL_LIGHT0);
        glLightfv(GL_LIGHT0, GL_POSITION, asFlippedFloatBuffer(new float[] { 0, 0, 0, 0 }));
        glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
    }

    public void tick() {
        glLightfv(GL_LIGHT0, GL_POSITION, asFlippedFloatBuffer(new float[] { lights.get(0).position[0], lights.get(0).position[1], lights.get(0)
                .position[2], .1f }));
    }

    public void setLightLocation(float x, float y, float z) {
        lights.get(0).position[0] = x;
        lights.get(0).position[1] = y;
        lights.get(0).position[2] = z;
    }

    public void disable() {
        glDisable(GL_LIGHTING);
    }

    public void enable() {
        glEnable(GL_LIGHTING);
    }

    public static FloatBuffer asFlippedFloatBuffer(float[] values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }
}