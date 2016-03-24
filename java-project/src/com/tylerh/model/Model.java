package com.tylerh.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

public class Model {

	private List<Vector3f> verticies;
	private List<Vector3f> normals;
	private List<Face> faces;

	public Model() {
		verticies = new ArrayList<Vector3f>();
		normals = new ArrayList<Vector3f>();
		faces = new ArrayList<Face>();
	}

	public void addVerticies(Vector3f vector3f) {
		verticies.add(vector3f);
	}

	public void addNormals(Vector3f vector3f) {
		normals.add(vector3f);
	}

	public void addFaces(Vector3f verticies, Vector3f normals) {
		faces.add(new Face(verticies, normals));
	}

	public List<Vector3f> getVerticies() {
		return verticies;
	}

	public List<Vector3f> getNormals() {
		return normals;
	}

	public List<Face> getFaces() {
		return faces;
	}
}
