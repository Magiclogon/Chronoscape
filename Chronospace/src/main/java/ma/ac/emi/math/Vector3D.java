package ma.ac.emi.math;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
	
	public static RayRectCollisionResponse rayRectIntersection(Vector3D start, Vector3D end, Rectangle rect) {

	    // Convert center-based rectangle to min/max
	    double halfW = rect.getWidth() / 2.0;
	    double halfH = rect.getHeight() / 2.0;

	    double minX = rect.getX() - halfW;
	    double maxX = rect.getX() + halfW;

	    double minY = rect.getY() - halfH;
	    double maxY = rect.getY() + halfH;

	    Vector3D dir = end.sub(start);

	    double tNear = Double.NEGATIVE_INFINITY;
	    double tFar  = Double.POSITIVE_INFINITY;

	    Vector3D normalNear = null;

	    // ---- X slab ----
	    if (dir.getX() != 0) {
	        double tx1 = (minX - start.getX()) / dir.getX();
	        double tx2 = (maxX - start.getX()) / dir.getX();

	        double t1 = Math.min(tx1, tx2);
	        double t2 = Math.max(tx1, tx2);

	        Vector3D n1 = tx1 < tx2 ? new Vector3D(-1, 0) : new Vector3D(1, 0);

	        if (t1 > tNear) {
	            tNear = t1;
	            normalNear = n1;
	        }
	        tFar = Math.min(tFar, t2);
	    } else {
	        if (start.getX() < minX || start.getX() > maxX)
	            return new RayRectCollisionResponse(null, false, null, null);
	    }

	    // ---- Y slab ----
	    if (dir.getY() != 0) {
	        double ty1 = (minY - start.getY()) / dir.getY();
	        double ty2 = (maxY - start.getY()) / dir.getY();

	        double t1 = Math.min(ty1, ty2);
	        double t2 = Math.max(ty1, ty2);

	        Vector3D n1 = ty1 < ty2 ? new Vector3D(0, -1) : new Vector3D(0, 1);

	        if (t1 > tNear) {
	            tNear = t1;
	            normalNear = n1;
	        }
	        tFar = Math.min(tFar, t2);
	    } else {
	        if (start.getY() < minY || start.getY() > maxY)
	            return new RayRectCollisionResponse(null, false, null, null);
	    }

	    // ---- Final checks ----
	    if (tNear > tFar) return new RayRectCollisionResponse(null, false, null, null);
	    if (tFar < 0)     return new RayRectCollisionResponse(null, false, null, null);

	    Vector3D contactPoint = start.add(dir.mult(tNear));

	    return new RayRectCollisionResponse(tNear, true, contactPoint, normalNear);
	}


	
	public static class RayRectCollisionResponse{
		public final Double t;
		public final boolean inCollision;
		public final Vector3D contactPoint;
		public final Vector3D contactNormal;
		
		RayRectCollisionResponse(Double t, boolean inCollision, Vector3D contactPoint, Vector3D contactNormal){
			this.t = t;
			this.inCollision = inCollision;
			this.contactPoint = contactPoint;
			this.contactNormal = contactNormal;
		}
	}

}
