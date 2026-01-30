package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class BlurEffect implements PostEffect {
    private Shader blurShader;
    private boolean horizontal;
    private float radius;  // Blur radius/strength
    
    public BlurEffect(GL3 gl, boolean horizontal) {
        this(gl, horizontal, 1.0f);  // Default radius
    }
    
    public BlurEffect(GL3 gl, boolean horizontal, float radius) {
        this.horizontal = horizontal;
        this.radius = radius;
        this.blurShader = Shader.load(gl, "post.vert", "blur.frag");
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    @Override
    public void apply(GL3 gl, int inputTexture, FullscreenQuad quad) {
        blurShader.use(gl);
        blurShader.setInt(gl, "uTexture", 0);
        blurShader.setInt(gl, "uHorizontal", horizontal ? 1 : 0);
        blurShader.setFloat(gl, "uRadius", radius);
        
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, inputTexture);
        
        quad.draw(gl, inputTexture);
    }
    
    @Override
    public void dispose(GL3 gl) {
        if (blurShader != null) {
            blurShader.dispose(gl);
        }
    }
}