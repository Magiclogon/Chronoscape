package ma.ac.emi.gamelogic.physics;

import ma.ac.emi.math.Vector3D;

public class AABB {
    public Vector3D center;
    public Vector3D half;

    public AABB(Vector3D center, Vector3D half) {
        this.center = center;
        this.half = half;
    }

    public AABB() {
    	this(new Vector3D(), new Vector3D());
	}

	public boolean intersects(AABB other) {
        return Math.abs(center.getX() - other.center.getX()) < (half.getX() + other.half.getX())
            && Math.abs(center.getY() - other.center.getY()) < (half.getY() + other.half.getY());
    }
}

