package ma.ac.emi.glgraphics.color;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.Shader;

public class SpriteColorCorrection {
    // Basic RGBA tint
    public float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
    
    // HSV adjustments
    public float hueShift = 0.0f;        // -1.0 to 1.0 (shifts hue around color wheel)
    public float saturation = 1.0f;      // 0.0 to 2.0+ (0 = grayscale, 1 = normal, >1 = oversaturated)
    public float value = 1.0f;           // 0.0 to 2.0+ (brightness multiplier)
    
    // Brightness/Contrast
    public float brightness = 0.0f;      // -1.0 to 1.0 (additive brightness)
    public float contrast = 1.0f;        // 0.0 to 2.0+ (1 = normal)
    
    // Color replacement
    public boolean useColorReplacement = false;
    public float[] targetColor = {1.0f, 1.0f, 1.0f};  // Color to replace
    public float[] replacementColor = {1.0f, 1.0f, 1.0f}; // Color to replace with
    public float colorTolerance = 0.1f;  // How close colors need to be (0-1)
    
    // Presets
    public static final SpriteColorCorrection NORMAL = new SpriteColorCorrection();
    public static final SpriteColorCorrection RED_TINT = new SpriteColorCorrection().setTint(1, 0.5f, 0.5f);
    public static final SpriteColorCorrection BLUE_TINT = new SpriteColorCorrection().setTint(0.7f, 0.7f, 1);
    public static final SpriteColorCorrection DAMAGE = new SpriteColorCorrection().setTint(1.5f, 0.5f, 0.5f);
    public static final SpriteColorCorrection POISON = new SpriteColorCorrection().setHueShift(0.3f).setSaturation(1.5f);
    public static final SpriteColorCorrection FROZEN = new SpriteColorCorrection().setTint(0.7f, 0.9f, 1.2f).setSaturation(0.6f);
    public static final SpriteColorCorrection INVERTED = new SpriteColorCorrection().setContrast(-1.0f);
	public static final SpriteColorCorrection BLACK = new SpriteColorCorrection().setBrightness(-1);
    
    public SpriteColorCorrection() {}
    
    public SpriteColorCorrection(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    
    public SpriteColorCorrection(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }
    
    // Builder-style setters
    public SpriteColorCorrection setTint(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }
    
    public SpriteColorCorrection setTint(float r, float g, float b) {
        return setTint(r, g, b, 1.0f);
    }
    
    public SpriteColorCorrection setHueShift(float hueShift) {
        this.hueShift = hueShift;
        return this;
    }
    
    public SpriteColorCorrection setSaturation(float saturation) {
        this.saturation = saturation;
        return this;
    }
    
    public SpriteColorCorrection setValue(float value) {
        this.value = value;
        return this;
    }
    
    public SpriteColorCorrection setBrightness(float brightness) {
        this.brightness = brightness;
        return this;
    }
    
    public SpriteColorCorrection setContrast(float contrast) {
        this.contrast = contrast;
        return this;
    }
    
    public SpriteColorCorrection setColorReplacement(float[] target, float[] replacement, float tolerance) {
        this.useColorReplacement = true;
        this.targetColor = target;
        this.replacementColor = replacement;
        this.colorTolerance = tolerance;
        return this;
    }
    
    public SpriteColorCorrection setColorReplacement(float tr, float tg, float tb,
                                               float rr, float rg, float rb, float tolerance) {
        return setColorReplacement(
            new float[]{tr, tg, tb},
            new float[]{rr, rg, rb},
            tolerance
        );
    }
    
    public SpriteColorCorrection disableColorReplacement() {
        this.useColorReplacement = false;
        return this;
    }
    
    // Factory methods
    public static SpriteColorCorrection grayscale(float intensity) {
        return new SpriteColorCorrection().setSaturation(0.0f).setValue(intensity);
    }
    
    public static SpriteColorCorrection sepia() {
        return new SpriteColorCorrection()
            .setTint(1.2f, 1.0f, 0.8f)
            .setSaturation(0.6f);
    }
    
    public static SpriteColorCorrection nightVision() {
        return new SpriteColorCorrection()
            .setTint(0.3f, 1.5f, 0.3f)
            .setSaturation(0.4f)
            .setContrast(1.3f);
    }
    
    public static SpriteColorCorrection heatVision() {
        return new SpriteColorCorrection()
            .setHueShift(-0.1f)
            .setSaturation(2.0f)
            .setContrast(1.5f);
    }
    
    public SpriteColorCorrection copy() {
        SpriteColorCorrection copy = new SpriteColorCorrection(r, g, b, a);
        copy.hueShift = this.hueShift;
        copy.saturation = this.saturation;
        copy.value = this.value;
        copy.brightness = this.brightness;
        copy.contrast = this.contrast;
        copy.useColorReplacement = this.useColorReplacement;
        copy.targetColor = this.targetColor.clone();
        copy.replacementColor = this.replacementColor.clone();
        copy.colorTolerance = this.colorTolerance;
        return copy;
    }
    
    public void apply(GL3 gl, Shader shader) {
        // Basic tint
        shader.setVec4(gl, "uColorTint", r, g, b, a);
        
        // HSV adjustments
        shader.setFloat(gl, "uHueShift", hueShift);
        shader.setFloat(gl, "uSaturation", saturation);
        shader.setFloat(gl, "uValue", value);
        
        // Brightness/Contrast
        shader.setFloat(gl, "uBrightness", brightness);
        shader.setFloat(gl, "uContrast", contrast);
        
        // Color replacement
        shader.setInt(gl, "uUseColorReplacement", useColorReplacement ? 1 : 0);
        if (useColorReplacement) {
            shader.setVec3(gl, "uTargetColor", targetColor[0], targetColor[1], targetColor[2]);
            shader.setVec3(gl, "uReplacementColor", replacementColor[0], replacementColor[1], replacementColor[2]);
            shader.setFloat(gl, "uColorTolerance", colorTolerance);
        }
    }
    
    public static void resetUniforms(GL3 gl, Shader shader) {
        shader.setVec4(gl, "uColorTint", 1.0f, 1.0f, 1.0f, 1.0f);
        shader.setFloat(gl, "uHueShift", 0.0f);
        shader.setFloat(gl, "uSaturation", 1.0f);
        shader.setFloat(gl, "uValue", 1.0f);
        shader.setFloat(gl, "uBrightness", 0.0f);
        shader.setFloat(gl, "uContrast", 1.0f);
        shader.setInt(gl, "uUseColorReplacement", 0);
    }
}