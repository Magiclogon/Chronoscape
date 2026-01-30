package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class GammaToLinearEffect implements PostEffect {
    private Shader shader;
    public GammaToLinearEffect(GL3 gl) { 
    	this.shader = Shader.load(gl, "post.vert", "linear.frag");
    	
    }
    
    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        shader.use(gl);
        quad.draw(gl, sourceTexture);
    }

	@Override
	public void dispose(GL3 gl) {
		// TODO Auto-generated method stub
		
	}
}
