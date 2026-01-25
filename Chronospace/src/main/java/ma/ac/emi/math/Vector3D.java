package ma.ac.emi.math;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Vector3D {
	private double x, y, z;
	
	public Vector3D() {
		this(0, 0, 0);
	}

	public Vector3D(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public Vector3D(double x, double y) {
		this.x = x; this.y = y; this.z = 0;
	}
	
	public Vector3D(Vector3D v) {
		this(v.getX(), v.getY(), v.getZ());
	}
	
	public void init() {
		setX(0); setY(0); setZ(0);
	}
	
	public Vector3D add(Vector3D v) {
		return new Vector3D(this.getX() + v.getX(), this.getY() + v.getY(), this.getZ() + v.getZ());
	}
	
	public Vector3D sub(Vector3D v) {
		return add(v.mult(-1));
	}
	
	public Vector3D mult(double a) {
		return new Vector3D(this.getX() * a, this.getY() * a, this.getZ() * a);
	}
	
	public double dotP(Vector3D v) {
		return this.getX() * v.getX() + this.getY() * v.getY() + this.getZ() * v.getZ();
	}

	public double norm() {
		return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2) + Math.pow(this.getZ(), 2));
	}

	public Vector3D normalize() {
		return new Vector3D(this.getX() / norm(), this.getY() / norm(), this.getZ() / norm());
	}

	public double distance(Vector3D v) {
		return (this.sub(v)).norm();
	}

	public Vector3D applyTransform(AffineTransform transform) {
	    Point2D.Double src = new Point2D.Double(x, y);
	    Point2D.Double dst = new Point2D.Double();
	    transform.transform(src, dst);
	    return new Vector3D(dst.x, dst.y);
	}
	
	public static Vector3D lerp(Vector3D a, Vector3D b, double t) {
		return a.add(b.sub(a).mult(t));
	}
	
	public static Vector3D randomUnit2() {
		double theta = Math.random() * 2 * Math.PI;
		return new Vector3D(Math.cos(theta), Math.sin(theta));
	}
	
	

}
