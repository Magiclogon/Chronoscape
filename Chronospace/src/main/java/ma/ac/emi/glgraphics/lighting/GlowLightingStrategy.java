package ma.ac.emi.glgraphics.lighting;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.Shader;

public class GlowLightingStrategy implements LightingStrategy {
    private float intensity;
    private float[] color; // RGB values (0-1)
    
    public GlowLightingStrategy(float intensity, float r, float g, float b) {
        this.intensity = intensity;
        this.color = new float[]{r, g, b};
    }
    
    public GlowLightingStrategy(float intensity) {
        this(intensity, 1.0f, 1.0f, 1.0f); // White glow by default
    }
    
    @Override
    public void applyLighting(GL3 gl, Shader shader) {
        shader.setFloat(gl, "uEmission", intensity);
        shader.setVec3(gl, "uEmissionColor", color[0], color[1], color[2]);
    }
    
    @Override
    public boolean shouldBloom() {
        return intensity > 0.1f; // Only bloom if significant glow
    }
    
    @Override
    public float getEmissionIntensity() {
        return intensity;
    }
    
    @Override
    public Light getLight() {
    	Light l = new Light();
    	l.setColor(color[0], color[1], color[2]);
    	
    	return l;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public void setColor(float r, float g, float b) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
    }
}