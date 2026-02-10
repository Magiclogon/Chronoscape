package ma.ac.emi.glgraphics.entitypost.config;

public class ColorReplacementConfig {
    private boolean enabled = false;
    private ColorConfig targetColor;
    private ColorConfig replacementColor;
    private float tolerance = 0.1f;
    
    // Getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public ColorConfig getTargetColor() { return targetColor; }
    public void setTargetColor(ColorConfig targetColor) { this.targetColor = targetColor; }
    public ColorConfig getReplacementColor() { return replacementColor; }
    public void setReplacementColor(ColorConfig replacementColor) { this.replacementColor = replacementColor; }
    public float getTolerance() { return tolerance; }
    public void setTolerance(float tolerance) { this.tolerance = tolerance; }
}
