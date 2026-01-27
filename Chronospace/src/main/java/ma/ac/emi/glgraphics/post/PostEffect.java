package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;

public interface PostEffect {
    void apply(GL3 gl, int sourceTexture, FullscreenQuad quad);
    void dispose(GL3 gl);
}
