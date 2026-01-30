package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;
import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;


public class BloomExtractEffect implements PostEffect {

    private final Shader extractShader;
    private float threshold;

    public BloomExtractEffect(GL3 gl, float threshold) {
        this.threshold = threshold;
        this.extractShader = Shader.load(gl, "post.vert", "bloom_extract.frag");
    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
        extractShader.use(gl);
        extractShader.setFloat(gl, "threshold", threshold);
        quad.draw(gl, sourceTexture);

    }


    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    @Override
    public void dispose(GL3 gl) {
    	extractShader.dispose(gl);
    }
}
