package ma.ac.emi.math;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	
	public Vector2D applyTransform(AffineTransform transform) {
	    Point2D.Double src = new Point2D.Double(x, y);
	    Point2D.Double dst = new Point2D.Double();
	    transform.transform(src, dst);
	    return new Vector2D(dst.x, dst.y);
	}
	
	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}
	
}
