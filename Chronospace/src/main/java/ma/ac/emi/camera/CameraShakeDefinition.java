package ma.ac.emi.camera;

public class CameraShakeDefinition {
	public double intensity;
	public double damping;
	
	public CameraShakeDefinition(double intensity, double damping) {
		this.intensity = intensity;
		this.damping = damping;
	}
	
	public CameraShakeDefinition(CameraShakeDefinition def) {
		this(def.intensity, def.damping);
	}
	
}
