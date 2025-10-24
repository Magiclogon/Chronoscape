package ma.ac.emi.camera;

import ma.ac.emi.math.Vector2D;

public abstract class GameDrawable {
	protected Vector2D screenPos;
	protected Vector2D scaleRatios;
	protected Camera camera;
	
	public GameDrawable(Camera camera) {
		screenPos = new Vector2D();
		scaleRatios = new Vector2D(1,1);
		this.camera = camera;
	}
	
	public abstract void camTransform();
	
	public Vector2D getScreenPos() {
		return screenPos;
	}

	public void setScreenPos(Vector2D screenPos) {
		this.screenPos = screenPos;
	}
	
	public Vector2D getScaleRatios() {
		return scaleRatios;
	}
	
	public void setScaleRatios(Vector2D scaleRatios) {
		this.scaleRatios = scaleRatios;
	}
	
}
