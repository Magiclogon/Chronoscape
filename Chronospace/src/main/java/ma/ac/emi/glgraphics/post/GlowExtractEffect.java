package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.FullscreenQuad;


public class GlowExtractEffect implements PostEffect {
    private final int glowBufferTextureId;
    
    public GlowExtractEffect(int glowBufferTextureId) {
        this.glowBufferTextureId = glowBufferTextureId;
    }
    
    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        quad.draw(gl, glowBufferTextureId);
    }
    
    @Override
    public void dispose(GL3 gl) {
    }
}