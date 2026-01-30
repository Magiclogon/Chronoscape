package ma.ac.emi.glgraphics.color;

public abstract class TemporaryColorEffect {
    protected float duration;
    protected float elapsed = 0;
    
    public TemporaryColorEffect(float duration) {
        this.duration = duration;
    }
    
    public void update(double step) {
        elapsed += step;
    }
    
    public boolean isFinished() {
        return elapsed >= duration;
    }
    
    public float getProgress() {
        return Math.min(elapsed / duration, 1.0f);
    }
    
    public abstract void apply(SpriteColorCorrection correction);
}


// Example: Damage tint that fades
class DamageTintEffect extends TemporaryColorEffect {
    public DamageTintEffect(float duration) {
        super(duration);
    }
    
    @Override
    public void apply(SpriteColorCorrection correction) {
        float t = 1.0f - getProgress();
        correction.r = Math.max(correction.r, 1.0f + 0.5f * t);
        correction.g *= 1.0f - 0.5f * t;
        correction.b *= 1.0f - 0.5f * t;
    }
}
