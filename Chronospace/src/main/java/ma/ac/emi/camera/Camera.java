package ma.ac.emi.camera;

import ma.ac.emi.math.Vector2D;

public class Camera {
	private Vector2D pos;
	private double width, height;
	
	public Camera(Vector2D pos, double w, double h) {
		this.pos = pos;
		this.width = w;
		this.height = h;
	}
}
