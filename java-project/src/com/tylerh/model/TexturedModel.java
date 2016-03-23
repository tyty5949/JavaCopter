package com.blockydigital.engine.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class TexturedModel {

	private List<Vector3f> verticies;
	private List<Vector2f> texverticies;
	private List<Vector3f> normals;
	private List<TexturedFace> faces;

	public TexturedModel() {
		verticies = new ArrayList<Vector3f>();
		texverticies = new ArrayList<Vector2f>();
		normals = new ArrayList<Vector3f>();
		faces = new ArrayList<TexturedFace>();
	}

	public void addVerticies(Vector3f vector3f) {
		verticies.add(vector3f);
	}

	public void addTexVerticies(Vector2f vector2f) {
		texverticies.add(vector2f);
	}

	public void addNormals(Vector3f vector3f) {
		normals.add(vector3f);
	}

	public void addFaces(Vector3f verticies, Vector3f texverticies, Vector3f normals) {
		faces.add(new TexturedFace(verticies, texverticies, normals));
	}

	public List<Vector3f> getVerticies() {
		return verticies;
	}

	public List<Vector2f> getTexVerticies() {
		return texverticies;
	}

	public List<Vector3f> getNormals() {
		return normals;
	}

	public List<TexturedFace> getFaces() {
		return faces;
	}
}
