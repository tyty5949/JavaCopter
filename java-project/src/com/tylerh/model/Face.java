package com.blockydigital.engine.model;

import org.lwjgl.util.vector.Vector3f;

public class Face {

	private Vector3f vertex;
	private Vector3f normal;

	public Face(Vector3f vertex, Vector3f normal) {
		this.vertex = vertex;
		this.normal = normal;
	}

	public Vector3f getVertex() {
		return vertex;
	}

	public Vector3f getNormal() {
		return normal;
	}

}
