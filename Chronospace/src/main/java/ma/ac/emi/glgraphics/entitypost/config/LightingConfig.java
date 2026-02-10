package ma.ac.emi.glgraphics.entitypost.config;

public class LightingConfig {
    private String type; // "glow", "bloom_glow", "pulsating_glow", "none"
    private Float intensity;
    private Float baseIntensity;
    private Float pulseSpeed;
    private Float pulseAmount;
    private Float bloomBoost;
    private ColorConfig color;
    
    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Float getIntensity() { return intensity; }
    public void setIntensity(Float intensity) { this.intensity = intensity; }
    public Float getBaseIntensity() { return baseIntensity; }
    public void setBaseIntensity(Float baseIntensity) { this.baseIntensity = baseIntensity; }
    public Float getPulseSpeed() { return pulseSpeed; }
    public void setPulseSpeed(Float pulseSpeed) { this.pulseSpeed = pulseSpeed; }
    public Float getPulseAmount() { return pulseAmount; }
    public void setPulseAmount(Float pulseAmount) { this.pulseAmount = pulseAmount; }
    public Float getBloomBoost() { return bloomBoost; }
    public void setBloomBoost(Float bloomBoost) { this.bloomBoost = bloomBoost; }
    public ColorConfig getColor() { return color; }
    public void setColor(ColorConfig color) { this.color = color; }
}
