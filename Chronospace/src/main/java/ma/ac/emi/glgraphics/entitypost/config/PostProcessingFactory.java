package ma.ac.emi.glgraphics.entitypost.config;

import ma.ac.emi.glgraphics.color.SpriteColorCorrection;
import ma.ac.emi.glgraphics.lighting.*;

public class PostProcessingFactory {
    
    public static SpriteColorCorrection createColorCorrection(PostProcessingDetails details) {
        if (details == null || details.getColorCorrection() == null) {
            return SpriteColorCorrection.NORMAL;
        }
        
        SpriteColorCorrection cc = new SpriteColorCorrection();
        ColorCorrectionConfig config = details.getColorCorrection();
        
        // Apply tint
        if (config.getTint() != null) {
            TintConfig tint = config.getTint();
            cc.setTint(tint.getR(), tint.getG(), tint.getB(), tint.getA());
        }
        
        // Apply HSV
        if (config.getHueShift() != null) {
            cc.setHueShift(config.getHueShift());
        }
        if (config.getSaturation() != null) {
            cc.setSaturation(config.getSaturation());
        }
        if (config.getValue() != null) {
            cc.setValue(config.getValue());
        }
        
        // Apply brightness/contrast
        if (config.getBrightness() != null) {
            cc.setBrightness(config.getBrightness());
        }
        if (config.getContrast() != null) {
            cc.setContrast(config.getContrast());
        }
        
        // Apply color replacement
        if (details.getColorReplacement() != null && details.getColorReplacement().isEnabled()) {
            ColorReplacementConfig cr = details.getColorReplacement();
            ColorConfig target = cr.getTargetColor();
            ColorConfig replacement = cr.getReplacementColor();
            
            cc.setColorReplacement(
                target.getR(), target.getG(), target.getB(),
                replacement.getR(), replacement.getG(), replacement.getB(),
                cr.getTolerance()
            );
        }
        
        return cc;
    }
    
    public static LightingStrategy createLightingStrategy(PostProcessingDetails details) {
        if (details == null || details.getLighting() == null) {
            return new NoLightingStrategy();
        }
        
        LightingConfig config = details.getLighting();
        String type = config.getType() != null ? config.getType().toLowerCase() : "none";
        
        // Get color (default white)
        float r = 1.0f, g = 1.0f, b = 1.0f;
        if (config.getColor() != null) {
            r = config.getColor().getR();
            g = config.getColor().getG();
            b = config.getColor().getB();
        }
        
        switch (type) {
            case "glow":
                float intensity = config.getIntensity() != null ? config.getIntensity() : 1.0f;
                return new GlowLightingStrategy(intensity, r, g, b);
                
            case "pulsating_glow":
                float baseIntensity = config.getBaseIntensity() != null ? config.getBaseIntensity() : 0.5f;
                float pulseSpeed = config.getPulseSpeed() != null ? config.getPulseSpeed() : 2.0f;
                float pulseAmount = config.getPulseAmount() != null ? config.getPulseAmount() : 0.3f;
                return new PulsatingGlowStrategy(baseIntensity, pulseSpeed, pulseAmount, r, g, b);
                
            case "none":
            default:
                return new NoLightingStrategy();
        }
    }
}