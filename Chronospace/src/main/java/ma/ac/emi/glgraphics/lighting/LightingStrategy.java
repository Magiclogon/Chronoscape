package ma.ac.emi.glgraphics.lighting;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.Shader;

public interface LightingStrategy {
   
    void applyLighting(GL3 gl, Shader shader);
    
   
    boolean shouldBloom();
        
    float getEmissionIntensity();
    
    Light getLight();
}