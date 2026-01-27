package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class BloomCombineEffect implements PostEffect {
    private final Shader shader;
    private final int originalSceneTexture;
    private float intensity = 1.0f;

    public BloomCombineEffect(GL3 gl, int originalSceneTexture) {
        this.originalSceneTexture = originalSceneTexture;
        this.shader = Shader.load(gl, "post.vert", "bloom_combine.frag");
    }

    @Override
    public void apply(GL3 gl, int blurredHighlightsTexture, FullscreenQuad quad) {
        shader.use(gl);
        
        // Slot 0: The blurred glow from the Ping-Pong chain
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, blurredHighlightsTexture);
        shader.setInt(gl, "bloomBlur", 0);
        
        // Slot 1: The original crisp scene
        gl.glActiveTexture(GL3.GL_TEXTURE1);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, originalSceneTexture);
        shader.setInt(gl, "originalScene", 1);

        shader.setFloat(gl, "intensity", intensity);

        quad.draw(gl, blurredHighlightsTexture);
        
        // Clean up slots
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }

    public void setIntensity(float intensity) { this.intensity = intensity; }

    @Override
    public void dispose(GL3 gl) { }
}