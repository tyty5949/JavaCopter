package com.tylerh;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * Created by tsh5949 on 3/23/2016.
 * Last edited on Mar 23, 2016 by tsh5949.
 *
 * Description:
 */

public class GLU {

    private static final float PI = (float) Math.PI;
    private static final float[] IDENTITY_MATRIX =
            new float[]{
                    1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f};

    private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    private static void __gluMakeIdentityf(FloatBuffer m) {
        int oldPos = m.position();
        m.put(IDENTITY_MATRIX);
        m.position(oldPos);
    }

    public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
        float sine, cotangent, deltaZ;
        float radians = fovy / 2 * GLU.PI / 180;

        deltaZ = zFar - zNear;
        sine = (float) Math.sin(radians);

        if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
            return;
        }

        cotangent = (float) Math.cos(radians) / sine;

        __gluMakeIdentityf(matrix);

        matrix.put(0, cotangent / aspect);
        matrix.put(4 + 1, cotangent);
        matrix.put(2 * 4 + 2, -(zFar + zNear) / deltaZ);
        matrix.put(2 * 4 + 3, -1);
        matrix.put(3 * 4 + 2, -2 * zNear * zFar / deltaZ);
        matrix.put(3 * 4 + 3, 0);

        GL11.glMultMatrixf(matrix);
    }
}
