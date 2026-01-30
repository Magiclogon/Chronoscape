package ma.ac.emi.glgraphics.lighting;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.Shader;

public class NoLightingStrategy implements LightingStrategy {
    @Override
    public void applyLighting(GL3 gl, Shader shader) {
        shader.setFloat(gl, "uEmission", 0.0f);
        shader.setVec3(gl, "uEmissionColor", 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public boolean shouldBloom() {
        return false;
    }
    
    @Override
    public float getEmissionIntensity() {
        return 0.0f;
    }

	@Override
	public Light getLight() {
		return null;
	}
    
    
}