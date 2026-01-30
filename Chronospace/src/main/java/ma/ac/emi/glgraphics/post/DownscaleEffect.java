package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;

public class DownscaleEffect implements PostEffect {
    private final int factor;
    
    public DownscaleEffect(GL3 gl, int factor) {
        this.factor = factor;
    }
    
    public int getFactor() {
        return factor;
    }
    
    @Override
    public void apply(GL3 gl, int inputTexture, FullscreenQuad quad) {
        quad.draw(gl, inputTexture);
    }
    
    @Override
    public void dispose(GL3 gl) {
        // Nothing to dispose
    }
}