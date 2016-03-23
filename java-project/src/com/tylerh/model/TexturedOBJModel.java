package com.blockydigital.engine.model;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.blockydigital.engine.util.OBJLoader;

public class TexturedOBJModel {

	private int objectDisplayList;

	public TexturedOBJModel() {
		objectDisplayList = -1;
	}

	public TexturedOBJModel(String file) {
		objectDisplayList = glGenLists(1);
		glNewList(objectDisplayList, GL_COMPILE);
		{
			TexturedModel m = null;
			try {
				m = OBJLoader.loadTexturedModel(new File(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			glColor3f(1.0f, 1.0f, 1.0f);
			glBegin(GL_TRIANGLES);
			for (TexturedFace face : m.getFaces()) {
				Vector3f n1 = m.getNormals().get((int) face.getNormal().x - 1);
				glNormal3f(n1.x, n1.y, n1.z);
				Vector2f t1 = m.getTexVerticies().get((int) face.getTexVertex().x - 1);
				glTexCoord2f(t1.x, t1.y);
				Vector3f v1 = m.getVerticies().get((int) face.getVertex().x - 1);
				glVertex3f(v1.x, v1.y, v1.z);
				Vector3f n2 = m.getNormals().get((int) face.getNormal().y - 1);
				glNormal3f(n2.x, n2.y, n2.z);
				Vector2f t2 = m.getTexVerticies().get((int) face.getTexVertex().y - 1);
				glTexCoord2f(t2.x, t2.y);
				Vector3f v2 = m.getVerticies().get((int) face.getVertex().y - 1);
				glVertex3f(v2.x, v2.y, v2.z);
				Vector3f n3 = m.getNormals().get((int) face.getNormal().z - 1);
				glNormal3f(n3.x, n3.y, n3.z);
				Vector2f t3 = m.getTexVerticies().get((int) face.getTexVertex().z - 1);
				glTexCoord2f(t3.x, t3.y);
				Vector3f v3 = m.getVerticies().get((int) face.getVertex().z - 1);
				glVertex3f(v3.x, v3.y, v3.z);
			}
			glEnd();
		}
		glEndList();
	}

	public void render() {
		glCallList(objectDisplayList);
	}
}
