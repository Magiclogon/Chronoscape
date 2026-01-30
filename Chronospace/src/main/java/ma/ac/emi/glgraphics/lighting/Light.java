package ma.ac.emi.glgraphics.lighting;

public class Light{
    public float x, y;  
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
    	this(0, 0, 0);
    }
    
    public Light(float x, float y, float radius, float r, float g, float b, float intensity) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.r = r;
        this.g = g;
        this.b = b;
        this.intensity = intensity;
        this.type = LightType.POINT;
    }
    
    public Light(float x, float y, float radius) {
        this(x, y, radius, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public Light(float radius) {
    	this(0, 0, radius, 1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
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