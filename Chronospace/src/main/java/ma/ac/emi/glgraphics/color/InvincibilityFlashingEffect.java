package ma.ac.emi.glgraphics.color;

public class InvincibilityFlashingEffect extends TemporaryColorEffect{
	private double flashingFrequency;

	public InvincibilityFlashingEffect(double duration, double flashingFrequency) {
		super((float)duration);
		this.flashingFrequency = flashingFrequency;
	}

	@Override
	public void apply(SpriteColorCorrection correction) {
		float t = getProgress();
		correction.a = (float) ((1 + Math.sin(2*Math.PI*flashingFrequency*t*duration)) * 0.5);
	}

}
