package com.tylerh.lwjgl;

public class LightSource {

    public float[] position;

    public LightSource(float x, float y, float z, float intensity) {
        position = new float[4];
        position[0] = x;
        position[1] = y;
        position[2] = z;
        position[3] = intensity;
    }
}