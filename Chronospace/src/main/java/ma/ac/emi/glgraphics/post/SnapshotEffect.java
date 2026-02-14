package ma.ac.emi.glgraphics.post;

import com.jogamp.opengl.GL3;

import ma.ac.emi.glgraphics.FullscreenQuad;
import ma.ac.emi.glgraphics.Shader;

public class SnapshotEffect implements PostEffect {
    private final PostProcessor processor;
    private Shader shader;

    public SnapshotEffect(GL3 gl, PostProcessor processor) {
        this.processor = processor;
        this.shader = Shader.load(gl, "post.vert", "copy.frag");

    }

    @Override
    public void apply(GL3 gl, int sourceTexture, FullscreenQuad quad) {
    	shader.use(gl);
        quad.draw(gl, sourceTexture);
        processor.saveSnapshot(gl, sourceTexture);

    }

    @Override public void dispose(GL3 gl) {}
}