package ma.ac.emi.glgraphics.lighting;

import ma.ac.emi.math.Vector3D;

public class Light{
    public Vector3D pos;  
    public float r, g, b;  
    public float radius;  
    public float intensity; 
    public LightType type;
    private boolean enabled = true;
    
    public enum LightType {
        POINT,      
        SPOT,       // Directional cone
        AMBIENT     // Fills entire screen
    }
    
    public Light() {
    	this(0);
    }
    
    public Light(Vector3D pos, float radius, float r, float g, float b, float intensity) {
        this.pos = pos;
        this.radius = radius;
        this.r = r;
        this.g = g;
        this.b = b;
        this.intensity = intensity;
        this.type = LightType.POINT;
    }
    
    public Light(Vector3D pos, float radius) {
        this(pos, radius, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public Light(float radius) {
    	this(new Vector3D(), radius, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void setPosition(Vector3D pos) {
        this.pos = pos;
    }
    
    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}