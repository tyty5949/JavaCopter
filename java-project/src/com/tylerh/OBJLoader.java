package com.blockydigital.engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.blockydigital.engine.model.Model;
import com.blockydigital.engine.model.TexturedModel;

public class OBJLoader {
	public static Model loadModel(File file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Model model = new Model();
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.addVerticies(new Vector3f(x, y, z));
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.addNormals(new Vector3f(x, y, z));
			} else if (line.startsWith("f ")) {
				float x = Float.valueOf(line.split(" ")[1].split("/")[0]);
				float y = Float.valueOf(line.split(" ")[2].split("/")[0]);
				float z = Float.valueOf(line.split(" ")[3].split("/")[0]);
				Vector3f vertexIndices = new Vector3f(x, y, z);
				x = Float.valueOf(line.split(" ")[1].split("/")[2]);
				y = Float.valueOf(line.split(" ")[2].split("/")[2]);
				z = Float.valueOf(line.split(" ")[3].split("/")[2]);
				Vector3f vertexNormals = new Vector3f(x, y, z);
				model.addFaces(vertexIndices, vertexNormals);
			}
		}
		reader.close();
		return model;
	}

	public static TexturedModel loadTexturedModel(File file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		TexturedModel model = new TexturedModel();
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.addVerticies(new Vector3f(x, y, z));
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.addNormals(new Vector3f(x, y, z));
			} else if (line.startsWith("vt ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				model.addTexVerticies(new Vector2f(x, y));
			} else if (line.startsWith("f ")) {
				Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]), Float.valueOf(line.split(" ")[2].split("/")[0]), Float.valueOf(line.split(" ")[3].split("/")[0]));
				Vector3f textureIndicies = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[1]), Float.valueOf(line.split(" ")[2].split("/")[1]), Float.valueOf(line.split(" ")[3].split("/")[1]));
				Vector3f vertexNormals = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]), Float.valueOf(line.split(" ")[2].split("/")[2]), Float.valueOf(line.split(" ")[3].split("/")[2]));
				model.addFaces(vertexIndices, textureIndicies, vertexNormals);
			}
		}
		reader.close();
		return model;
	}
}
