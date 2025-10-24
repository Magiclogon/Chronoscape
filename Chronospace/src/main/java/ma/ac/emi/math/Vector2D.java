package ma.ac.emi.math;

public class Vector2D {
	private double x, y;
	
	public Vector2D() {
		this(0, 0);
	}

	public Vector2D(double x, double y) {
		this.x = x; this.y = y;
	}
	
	public void init() {
		setX(0); setY(0);
	}
	
	public Vector2D add(Vector2D v) {
		return new Vector2D(this.getX() + v.getX(), this.getY() + v.getY());
	}
	
	public Vector2D sub(Vector2D v) {
		return add(v.mult(-1));
	}
	
	public Vector2D mult(double a) {
		return new Vector2D(this.getX() * a, this.getY() * a);
	}
	
	public double dotP(Vector2D v) {
		return this.getX() * v.getX() + this.getY() * v.getY();
	}

	public double norm() {
		return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
	}

	public Vector2D normalize() {
		return new Vector2D(this.getX() / norm(), this.getY() / norm());
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
}
