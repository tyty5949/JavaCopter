package com.tylerh.model;

import org.joml.Vector3f;

public class TexturedFace {
	
	private Vector3f vertex;
	private Vector3f texvertex;
	private Vector3f normal;

	public TexturedFace(Vector3f vertex, Vector3f texvertex, Vector3f normal) {
		this.vertex = vertex;
		this.texvertex = texvertex;
		this.normal = normal;
	}

	public Vector3f getVertex() {
		return vertex;
	}

	public Vector3f getTexVertex() {
		return texvertex;
	}

	public Vector3f getNormal() {
		return normal;
	}
}
