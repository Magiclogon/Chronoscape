package ma.ac.emi.glgraphics.post.config;

public class PostProcessingDetails {
    private ColorCorrectionConfig colorCorrection;
    private LightingConfig lighting;
    private ColorReplacementConfig colorReplacement;
    
    // Getters and setters
    public ColorCorrectionConfig getColorCorrection() {
        return colorCorrection;
    }
    
    public void setColorCorrection(ColorCorrectionConfig colorCorrection) {
        this.colorCorrection = colorCorrection;
    }
    
    public LightingConfig getLighting() {
        return lighting;
    }
    
    public void setLighting(LightingConfig lighting) {
        this.lighting = lighting;
    }
    
    public ColorReplacementConfig getColorReplacement() {
        return colorReplacement;
    }
    
    public void setColorReplacement(ColorReplacementConfig colorReplacement) {
        this.colorReplacement = colorReplacement;
    }
}

