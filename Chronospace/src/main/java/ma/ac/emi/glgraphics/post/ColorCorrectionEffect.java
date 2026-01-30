package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class ColorCorrectionEffect implements PostEffect {
    private final Shader shader;
    
    // Default values
    private float brightness = 0.0f;
    private float contrast = 1.0f;
    private float saturation = 1.0f;
    private float exposure = 1.0f;

    public ColorCorrectionEffect(GL3 gl) {
        this.shader = Shader.load(gl, "post.vert", "color_correction.frag");
    }
    
    public ColorCorrectionEffect(GL3 gl, float brightness, float contrast, float saturation, float exposure) {
    	this.shader = Shader.load(gl, "post.vert", "color_correction.frag");
    	this.brightness = brightness;
    	this.contrast = contrast;
    	this.saturation = saturation;
    	this.exposure = exposure;
    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        shader.use(gl);
        
        shader.setFloat(gl, "brightness", brightness);
        shader.setFloat(gl, "contrast", contrast);
        shader.setFloat(gl, "saturation", saturation);
        shader.setFloat(gl, "exposure", exposure);

        quad.draw(gl, sourceTexture);
    }

    public void setBrightness(float brightness) { this.brightness = brightness; }
    public void setContrast(float contrast) { this.contrast = contrast; }
    public void setSaturation(float saturation) { this.saturation = saturation; }
    public void setExposure(float exposure) { this.exposure = exposure; }

    @Override
    public void dispose(GL3 gl) {
    	shader.dispose(gl);
    }
}