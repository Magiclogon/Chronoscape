package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

@Getter
@Setter
public class ColorCorrectionEffect implements PostEffect {

    private final Shader shader;
    
    private float r = 1;
    private float g = 1;
    private float b = 1;
    private float a = 1;

    private float brightness = 0.0f;
    private float contrast = 1.0f;
    
    private float saturation = 1.0f;
    private float hue = 0.0f;  
    private float value = 1f;
    
    public ColorCorrectionEffect(GL3 gl) {
        this.shader = Shader.load(gl, "post.vert", "color_correction.frag");
    }

    public ColorCorrectionEffect(
            GL3 gl,
            float r, float g, float b, float a,
            float brightness,
            float contrast,
            float saturation,
            float hue,
            float value
    ) {
        this(gl);
        this.r = r; this.g = g; this.b = b; this.a = a;
        this.brightness = brightness;
        this.contrast = contrast;
        this.saturation = saturation;
        this.hue = hue;
        this.value = value;
    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        shader.use(gl);
   
     // Basic tint
        shader.setVec4(gl, "uColorTint", r, g, b, a);
        
        // HSV adjustments
        shader.setFloat(gl, "uHueShift", hue);
        shader.setFloat(gl, "uSaturation", saturation);
        shader.setFloat(gl, "uValue", value);
        
        // Brightness/Contrast
        shader.setFloat(gl, "uBrightness", brightness);
        shader.setFloat(gl, "uContrast", contrast);
        quad.draw(gl, sourceTexture);
    }

    

    @Override
    public void dispose(GL3 gl) {
        shader.dispose(gl);
    }
}
