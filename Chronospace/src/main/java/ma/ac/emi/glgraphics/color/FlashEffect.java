package ma.ac.emi.glgraphics.color;

public class FlashEffect extends TemporaryColorEffect {
    private float intensity;
    
    public FlashEffect(float duration, float intensity) {
        super(duration);
        this.intensity = intensity;
    }
    
    @Override
    public void apply(SpriteColorCorrection correction) {
        float t = 1.0f - getProgress();
        correction.setBrightness(correction.brightness + intensity * t);
    }
}
