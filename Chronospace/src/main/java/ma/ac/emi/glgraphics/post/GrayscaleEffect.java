package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class GrayscaleEffect implements PostEffect {
    private Shader shader;

    public GrayscaleEffect(GL3 gl) {
    	this.shader = Shader.load(gl, "post.vert", "grayscale.frag");
    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        shader.use(gl);
        // You can set extra uniforms here if needed (e.g. intensity)
        quad.draw(gl, sourceTexture);
    }

    @Override
    public void dispose(GL3 gl) {
        shader.dispose(gl);
    }
}