package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class BlurEffect implements PostEffect {
    private final Shader shader;
    private final boolean horizontal;

    public BlurEffect(GL3 gl, boolean horizontal) {
        this.horizontal = horizontal;
        this.shader = Shader.load(gl, "post.vert", "blur.frag");
    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        shader.use(gl);
        
        // Tell the shader which direction we are blurring
        shader.setBoolean(gl, "horizontal", horizontal);
        
        quad.draw(gl, sourceTexture);
    }

    @Override
    public void dispose(GL3 gl) {
        // shader.dispose(gl);
    }

}