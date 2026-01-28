package ma.ac.emi.glgraphics.post.config;

public class ColorCorrectionConfig {
    private TintConfig tint;
    private Float hueShift;
    private Float saturation;
    private Float value;
    private Float brightness;
    private Float contrast;
    
    // Getters and setters
    public TintConfig getTint() { return tint; }
    public void setTint(TintConfig tint) { this.tint = tint; }
    public Float getHueShift() { return hueShift; }
    public void setHueShift(Float hueShift) { this.hueShift = hueShift; }
    public Float getSaturation() { return saturation; }
    public void setSaturation(Float saturation) { this.saturation = saturation; }
    public Float getValue() { return value; }
    public void setValue(Float value) { this.value = value; }
    public Float getBrightness() { return brightness; }
    public void setBrightness(Float brightness) { this.brightness = brightness; }
    public Float getContrast() { return contrast; }
    public void setContrast(Float contrast) { this.contrast = contrast; }
}