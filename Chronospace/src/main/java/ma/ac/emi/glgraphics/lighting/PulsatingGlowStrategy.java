package ma.ac.emi.glgraphics.lighting;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.Shader;

public class PulsatingGlowStrategy implements LightingStrategy {

	public PulsatingGlowStrategy(float baseIntensity, float pulseSpeed, float pulseAmount, float r, float g, float b) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyLighting(GL3 gl, Shader shader) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldBloom() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getEmissionIntensity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Light getLight() {
		// TODO Auto-generated method stub
		return null;
	}

}
