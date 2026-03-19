package ma.ac.emi.UI;

import ma.ac.emi.math.Vector3D;

public class FloatingText {

    public enum Preset {
        DODGED ("#7EC8E3", 22),   // icy blue
        DAMAGE ("#FF6B6B", 18),   // red
        HEAL   ("#7BFF7B", 18);   // green

        public final String hex;
        public final int    fontSize;
        Preset(String hex, int fontSize) { this.hex = hex; this.fontSize = fontSize; }
    }

    private static final double LIFETIME    = 1.1;  // seconds total
    private static final double RISE_SPEED  = 28.0; // world-units/s upward

    public final String  text;
    public final Preset  preset;

    private final Vector3D worldPos;

    private double age      = 0;
    private double offsetY  = 0;  
    private boolean dead    = false;

    public FloatingText(String text, Preset preset, Vector3D worldPos) {
        this.text     = text;
        this.preset   = preset;
        this.worldPos = new Vector3D(worldPos);
    }

    public void update(double step) {
        age     += step;
        offsetY -= RISE_SPEED * step;  
        if (age >= LIFETIME) dead = true;
    }

    /** Alpha 0→1→0 — peaks quickly then fades. */
    public float alpha() {
        double t = age / LIFETIME;
        return (float) Math.max(0, 1.0 - (t * t));
    }

    public Vector3D getWorldPos() { return worldPos; }
    public double   getOffsetY()  { return offsetY; }
    public boolean  isDead()      { return dead; }
}