package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class GlowCombineEffect implements PostEffect {
    private final Shader combineShader;
    private final int sceneTextureId;
    private float intensity;
    
    public GlowCombineEffect(GL3 gl, int sceneTextureId, float intensity) {
        this.sceneTextureId = sceneTextureId;
        this.intensity = intensity;
        this.combineShader = Shader.load(gl, "post.vert", "glow_combine.frag");
    }
    
    @Override
    public void apply(GL3 gl, int glowTexture, FullscreenQuad quad) {
    	combineShader.use(gl);
        
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, glowTexture);
        combineShader.setInt(gl, "glowTexture", 0);
        
        gl.glActiveTexture(GL3.GL_TEXTURE1);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, sceneTextureId);
        combineShader.setInt(gl, "sceneTexture", 1);

        combineShader.setFloat(gl, "intensity", intensity);

        quad.draw(gl, glowTexture);
        
        // Clean up slots
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    @Override
    public void dispose(GL3 gl) {
        combineShader.dispose(gl);
    }
}