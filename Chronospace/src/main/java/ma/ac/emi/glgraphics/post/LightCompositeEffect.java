package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class LightCompositeEffect implements PostEffect {
    private Shader shader;
    private int lightTextureId;
    
    public LightCompositeEffect(GL3 gl, int lightTextureId) {
        this.lightTextureId = lightTextureId;
        this.shader = Shader.load(gl, "post.vert", "light_composite.frag");
    }
    
    public void setLightTexture(int textureId) {
        this.lightTextureId = textureId;
    }
    
    @Override
    public void apply(GL3 gl, int inputTexture, FullscreenQuad quad) {
        shader.use(gl);
        shader.setInt(gl, "uSceneTexture", 0);
        shader.setInt(gl, "uLightTexture", 1);
        
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, inputTexture);
        
        gl.glActiveTexture(GL3.GL_TEXTURE1);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, lightTextureId);
        
        quad.draw(gl, inputTexture);
        
        gl.glActiveTexture(GL3.GL_TEXTURE0);
    }
    
    @Override
    public void dispose(GL3 gl) {
        if (shader != null) {
            shader.dispose(gl);
        }
    }
}