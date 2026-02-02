package ma.ac.emi.gamelogic.physics;

import ma.ac.emi.math.Vector3D;

public class RayHit {
    public final double t;
    public final Vector3D normal;

    public RayHit(double t, Vector3D normal) {
        this.t = t;
        this.normal = normal;
    }
}
